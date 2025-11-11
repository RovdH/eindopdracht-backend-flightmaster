package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.PassengerRequestDto;
import nl.helicenter.flightmaster.dto.PassengerResponseDto;
import nl.helicenter.flightmaster.dto.PassengerUpdateDto;
import nl.helicenter.flightmaster.model.Flight;
import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.model.Passenger;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.repository.FlightRepository;
import nl.helicenter.flightmaster.repository.PassengerRepository;
import nl.helicenter.flightmaster.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    @Mock
    PassengerRepository passengerRepository;
    @Mock
    FlightRepository flightRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    PassengerService passengerService;


    @Test
    void create_PassengerSavesAndReturnsDto() {
        PassengerRequestDto dto = new PassengerRequestDto();
        dto.setFirstName("Henk");
        dto.setLastName("Aalbers");
        dto.setEmail("henk@banaan.com");
        dto.setWeightKg(82.5);
        dto.setFlightId(10L);
        dto.setUserId(20L);

        Helicopter heli = new Helicopter();
        heli.setCapacity(4);

        Flight flight = new Flight();
        flight.setId(10L);
        flight.setHelicopter(heli);

        User user = new User();
        user.setId(20L);
        user.setEmail("henk.user@boeker.nl");

        when(flightRepository.findById(10L)).thenReturn(Optional.of(flight));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(passengerRepository.countByFlight_Id(10L)).thenReturn(2L);
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(inv -> {
            Passenger p = inv.getArgument(0);
            p.setId(111L);
            return p;
        });

        PassengerResponseDto resp = passengerService.create(dto);

        assertThat(resp.getId()).isEqualTo(111L);
        assertThat(resp.getFirstName()).isEqualTo("Henk");
        assertThat(resp.getLastName()).isEqualTo("Aalbers");
        assertThat(resp.getEmail()).isEqualTo("henk@banaan.com");
        assertThat(resp.getWeightKg()).isEqualTo(82.5);
        assertThat(resp.getFlightId()).isEqualTo(10L);
        assertThat(resp.getUserId()).isEqualTo(20L);

        ArgumentCaptor<Passenger> cap = ArgumentCaptor.forClass(Passenger.class);
        verify(passengerRepository).save(cap.capture());
        Passenger saved = cap.getValue();
        assertThat(saved.getFlight()).isSameAs(flight);
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getWeight()).isEqualTo(82.5);
    }

    @Test
    void create_nullRequest_throwsIllegalArgument() {
        assertThatThrownBy(() -> passengerService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leeg");
    }

    @Test
    void create_flightNotFound_throwsEntityNotFound() {
        PassengerRequestDto dto = new PassengerRequestDto();
        dto.setFlightId(666L);
        dto.setUserId(1L);
        dto.setWeightKg(70.0);
        dto.setFirstName("Antoon");
        dto.setLastName("Barten");
        dto.setEmail("ab@planet.nl");

        when(flightRepository.findById(666L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Flight 666 is niet gevonden of bestaat niet");
    }

    @Test
    void create_userNotFound_throwsEntityNotFound() {
        PassengerRequestDto dto = new PassengerRequestDto();
        dto.setFlightId(1L);
        dto.setUserId(999L);
        dto.setWeightKg(70.0);
        dto.setFirstName("Antoon");
        dto.setLastName("Barten");
        dto.setEmail("ab@banaan.nl");

        Helicopter heli = new Helicopter();
        heli.setCapacity(2);

        Flight flight = new Flight();
        flight.setId(1L);
        flight.setHelicopter(heli);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User 999 is niet gevonden of bestaat niet");
    }

    @Test
    void create_flightFull_throwsIllegalState() {
        PassengerRequestDto dto = new PassengerRequestDto();
        dto.setFlightId(1L);
        dto.setUserId(2L);
        dto.setWeightKg(70.0);
        dto.setFirstName("Frits");
        dto.setLastName("Banaan");
        dto.setEmail("frits@banaan.nl");

        Helicopter heli = new Helicopter();
        heli.setCapacity(2);

        Flight flight = new Flight();
        flight.setId(1L);
        flight.setHelicopter(heli);

        User user = new User();
        user.setId(2L);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(passengerRepository.countByFlight_Id(1L)).thenReturn(2L);

        assertThatThrownBy(() -> passengerService.create(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Flight is volgeboekt");
    }


    @Test
    void createBulk_nullOrEmpty_throwsIllegalArgument() {
        assertThatThrownBy(() -> passengerService.createBulk(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leeg");
        assertThatThrownBy(() -> passengerService.createBulk(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leeg");
    }

    @Test
    void createBulk_missingFlight_throwsEntityNotFound() {
        PassengerRequestDto dto1 = new PassengerRequestDto();
        dto1.setFlightId(100L);
        dto1.setUserId(1L);
        dto1.setWeightKg(80.0);
        dto1.setFirstName("Robert-Jan");
        dto1.setLastName("Elias");
        dto1.setEmail("backend@novi.nl");

        when(flightRepository.findAllById(Set.of(100L))).thenReturn(List.of()); // none found

        assertThatThrownBy(() -> passengerService.createBulk(List.of(dto1)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Flight 100 is niet gevonden of bestaat niet");
    }

    @Test
    void createBulk_capacityOverflow_throwsIllegalState() {
        PassengerRequestDto dto1 = new PassengerRequestDto();
        dto1.setFlightId(10L);
        dto1.setUserId(1L);
        dto1.setWeightKg(80.0);
        dto1.setFirstName("Robert-Jan");
        dto1.setLastName("Elias");
        dto1.setEmail("backend@novi.nl");

        PassengerRequestDto dto2 = new PassengerRequestDto();
        dto2.setFlightId(10L);
        dto2.setUserId(2L);
        dto2.setWeightKg(81.0);
        dto2.setFirstName("Carlijn");
        dto2.setLastName("Dirksen");
        dto2.setEmail("cd@email.nl");

        Helicopter heli = new Helicopter();
        heli.setCapacity(2);

        Flight flight = new Flight();
        flight.setId(10L);
        flight.setHelicopter(heli);

        when(flightRepository.findAllById(Set.of(10L))).thenReturn(List.of(flight));
        when(passengerRepository.countByFlight_Id(10L)).thenReturn(1L);

        assertThatThrownBy(() -> passengerService.createBulk(List.of(dto1, dto2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is volgeboekt")
                .hasMessageContaining("Gevraagd=2");
    }

    @Test
    void createBulk_userMissing_throwsEntityNotFound() {
        PassengerRequestDto dto1 = new PassengerRequestDto();
        dto1.setFlightId(10L);
        dto1.setUserId(1L);
        dto1.setWeightKg(80.0);
        dto1.setFirstName("Albert");
        dto1.setLastName("Kuip");
        dto1.setEmail("ak@planet.nl");

        Helicopter heli = new Helicopter();
        heli.setCapacity(3);

        Flight flight = new Flight();
        flight.setId(10L);
        flight.setHelicopter(heli);

        when(flightRepository.findAllById(Set.of(10L))).thenReturn(List.of(flight));
        when(passengerRepository.countByFlight_Id(10L)).thenReturn(0L);
        when(userRepository.findAllById(Set.of(1L))).thenReturn(List.of());

        assertThatThrownBy(() -> passengerService.createBulk(List.of(dto1)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User 1 is niet gevonden of bestaat niet");
    }

    @Test
    void createBulk_happyPath_mapsAllAndReturnsDtos() {
        PassengerRequestDto dto1 = new PassengerRequestDto();
        dto1.setFlightId(10L);
        dto1.setUserId(1L);
        dto1.setWeightKg(70.0);
        dto1.setFirstName("Albert");
        dto1.setLastName("Kuip");
        dto1.setEmail("ak@planet.nl");

        PassengerRequestDto dto2 = new PassengerRequestDto();
        dto2.setFlightId(10L);
        dto2.setUserId(2L);
        dto2.setWeightKg(71.0);
        dto2.setFirstName("Carlijn");
        dto2.setLastName("Dirksen");
        dto2.setEmail("cd@email.nl");

        PassengerRequestDto dto3 = new PassengerRequestDto();
        dto3.setFlightId(11L);
        dto3.setUserId(3L);
        dto3.setWeightKg(72.0);
        dto3.setFirstName("Jan");
        dto3.setLastName("Jansen");
        dto3.setEmail("jj@ziggo.nl");

        Helicopter heli1 = new Helicopter();
        heli1.setCapacity(5);
        Flight fl10 = new Flight();
        fl10.setId(10L);
        fl10.setHelicopter(heli1);

        Helicopter heli2 = new Helicopter();
        heli2.setCapacity(5);
        Flight fl20 = new Flight();
        fl20.setId(11L);
        fl20.setHelicopter(heli2);

        when(flightRepository.findAllById(Set.of(10L, 11L))).thenReturn(List.of(fl10, fl20));
        when(passengerRepository.countByFlight_Id(10L)).thenReturn(1L);
        when(passengerRepository.countByFlight_Id(11L)).thenReturn(0L);

        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("u1@voorbeeld.nl");
        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("u2@voorbeeld.nl");
        User u3 = new User();
        u3.setId(3L);
        u3.setEmail("u3@voorbeeld.nl");
        when(userRepository.findAllById(Set.of(1L, 2L, 3L))).thenReturn(List.of(u1, u2, u3));

        when(passengerRepository.saveAll(anyList())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            List<Passenger> list = inv.getArgument(0);
            long id = 100;
            for (Passenger p : list) p.setId(id++);
            return list;
        });

        List<PassengerResponseDto> result = passengerService.createBulk(List.of(dto1, dto2, dto3));

        assertThat(result).hasSize(3);
        assertThat(result).extracting(PassengerResponseDto::getEmail)
                .containsExactlyInAnyOrder("ak@planet.nl", "cd@email.nl", "jj@ziggo.nl");
        assertThat(result).extracting(PassengerResponseDto::getFlightId)
                .containsExactlyInAnyOrder(10L, 10L, 11L);
        assertThat(result).extracting(PassengerResponseDto::getUserId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(result).extracting(PassengerResponseDto::getId)
                .containsExactlyInAnyOrder(100L, 101L, 102L);
    }

    @Test
    void getById_found_returnsDto() {
        Helicopter heli = new Helicopter();
        heli.setCapacity(3);
        Flight flight = new Flight();
        flight.setId(9L);
        flight.setHelicopter(heli);
        User user = new User();
        user.setId(5L);

        Passenger p = new Passenger();
        p.setId(77L);
        p.setFirstName("Jan");
        p.setLastName("Jansen");
        p.setEmail("jj@ziggo.nl");
        p.setWeight(65.0);
        p.setFlight(flight);
        p.setUser(user);

        when(passengerRepository.findById(77L)).thenReturn(Optional.of(p));

        PassengerResponseDto dto = passengerService.getById(77L);

        assertThat(dto.getId()).isEqualTo(77L);
        assertThat(dto.getFirstName()).isEqualTo("Jan");
        assertThat(dto.getFlightId()).isEqualTo(9L);
        assertThat(dto.getUserId()).isEqualTo(5L);
    }

    @Test
    void getById_notFound_throws() {
        when(passengerRepository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> passengerService.getById(404L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Passenger 404 is niet gevonden of bestaat niet");
    }

    @Test
    void patch_updatesSelectedFields() {
        Helicopter heli = new Helicopter(); heli.setCapacity(4);
        Flight flight = new Flight(); flight.setId(77L); flight.setHelicopter(heli);
        User user = new User(); user.setId(9L);

        Passenger existing = new Passenger();
        existing.setId(7L);
        existing.setFirstName("Jan");
        existing.setLastName("Jansen");
        existing.setEmail("jan@ziggo.nl");
        existing.setWeight(70.0);
        existing.setFlight(flight);
        existing.setUser(user);

        when(passengerRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(inv -> inv.getArgument(0));

        PassengerUpdateDto dto = new PassengerUpdateDto();
        dto.setLastName("Janssen");
        dto.setWeightKg(75.5);

        PassengerResponseDto resp = passengerService.patch(7L, dto);

        assertThat(resp.getId()).isEqualTo(7L);
        assertThat(resp.getFirstName()).isEqualTo("Jan");
        assertThat(resp.getLastName()).isEqualTo("Janssen");
        assertThat(resp.getEmail()).isEqualTo("jan@ziggo.nl");
        assertThat(resp.getWeightKg()).isEqualTo(75.5);
        assertThat(resp.getFlightId()).isEqualTo(77L);
        assertThat(resp.getUserId()).isEqualTo(9L);

        ArgumentCaptor<Passenger> captor = ArgumentCaptor.forClass(Passenger.class);
        verify(passengerRepository).save(captor.capture());
        Passenger saved = captor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("Jan");
        assertThat(saved.getLastName()).isEqualTo("Janssen");
        assertThat(saved.getWeight()).isEqualTo(75.5);
    }

    @Test
    void patch_nothingFilledIn_keepsValues() {
        Helicopter heli = new Helicopter(); heli.setCapacity(4);
        Flight flight = new Flight(); flight.setId(11L); flight.setHelicopter(heli);
        User user = new User(); user.setId(22L);

        Passenger existing = new Passenger();
        existing.setId(8L);
        existing.setFirstName("Piet");
        existing.setLastName("Peeters");
        existing.setEmail("piet@ziggo.nl");
        existing.setWeight(80.0);
        existing.setFlight(flight);
        existing.setUser(user);

        when(passengerRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(inv -> inv.getArgument(0));

        PassengerUpdateDto dto = new PassengerUpdateDto();

        PassengerResponseDto resp = passengerService.patch(8L, dto);

        assertThat(resp.getId()).isEqualTo(8L);
        assertThat(resp.getFirstName()).isEqualTo("Piet");
        assertThat(resp.getLastName()).isEqualTo("Peeters");
        assertThat(resp.getEmail()).isEqualTo("piet@ziggo.nl");
        assertThat(resp.getWeightKg()).isEqualTo(80.0);
        assertThat(resp.getFlightId()).isEqualTo(11L);
        assertThat(resp.getUserId()).isEqualTo(22L);

        ArgumentCaptor<Passenger> captor = ArgumentCaptor.forClass(Passenger.class);
        verify(passengerRepository).save(captor.capture());
        Passenger saved = captor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("Piet");
        assertThat(saved.getLastName()).isEqualTo("Peeters");
        assertThat(saved.getEmail()).isEqualTo("piet@ziggo.nl");
        assertThat(saved.getWeight()).isEqualTo(80.0);
    }

    @Test
    void patch_notFound_throwsEntityNotFound() {
        when(passengerRepository.findById(123L)).thenReturn(Optional.empty());

        PassengerUpdateDto dto = new PassengerUpdateDto();
        dto.setFirstName("Nieuw");

        assertThatThrownBy(() -> passengerService.patch(123L, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Passenger 123 is niet gevonden of bestaat niet");
    }

    @Test
    void delete_notExists_throws() {
        when(passengerRepository.existsById(66L)).thenReturn(false);
        assertThatThrownBy(() -> passengerService.delete(66L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Passenger 66 is niet gevonden of bestaat niet");
        verify(passengerRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_exists_callsRepoDelete() {
        when(passengerRepository.existsById(67L)).thenReturn(true);
        passengerService.delete(67L);
        verify(passengerRepository).deleteById(67L);
    }

    @Test
    void listByFlight_mapsAll() {
        Helicopter heli = new Helicopter();
        heli.setCapacity(4);
        Flight flight = new Flight();
        flight.setId(77L);
        flight.setHelicopter(heli);
        User user = new User();
        user.setId(9L);

        Passenger p1 = new Passenger();
        p1.setId(1L);
        p1.setFirstName("Ali");
        p1.setLastName("Jansen");
        p1.setEmail("aj@ziggo.nl");
        p1.setWeight(60.0);
        p1.setFlight(flight);
        p1.setUser(user);

        Passenger p2 = new Passenger();
        p2.setId(2L);
        p2.setFirstName("Carlijn");
        p2.setLastName("Dirksen");
        p2.setEmail("cd@email.nl");
        p2.setWeight(61.0);
        p2.setFlight(flight);
        p2.setUser(user);

        when(passengerRepository.findAllByFlight_Id(77L)).thenReturn(List.of(p1, p2));

        List<PassengerResponseDto> list = passengerService.listByFlight(77L);

        assertThat(list).hasSize(2);
        assertThat(list).extracting(PassengerResponseDto::getId).containsExactlyInAnyOrder(1L, 2L);
        assertThat(list).extracting(PassengerResponseDto::getFlightId).containsOnly(77L);
        assertThat(list).extracting(PassengerResponseDto::getUserId).containsOnly(9L);
    }

    @Test
    void listByUser_mapsAll() {
        Helicopter heli = new Helicopter();
        heli.setCapacity(4);
        Flight flight = new Flight();
        flight.setId(88L);
        flight.setHelicopter(heli);
        User user = new User();
        user.setId(10L);

        Passenger p1 = new Passenger();
        p1.setId(3L);
        p1.setFirstName("Jan");
        p1.setLastName("Jansen");
        p1.setEmail("jj@ziggo.nl");
        p1.setWeight(62.0);
        p1.setFlight(flight);
        p1.setUser(user);

        Passenger p2 = new Passenger();
        p2.setId(4L);
        p2.setFirstName("Gerda");
        p2.setLastName("Hurk");
        p2.setEmail("gh@ziggo.nl");
        p2.setWeight(63.0);
        p2.setFlight(flight);
        p2.setUser(user);

        when(passengerRepository.findAllByUser_Id(10L)).thenReturn(List.of(p1, p2));

        List<PassengerResponseDto> list = passengerService.listByUser(10L);

        assertThat(list).hasSize(2);
        assertThat(list).extracting(PassengerResponseDto::getId).containsExactlyInAnyOrder(3L, 4L);
        assertThat(list).extracting(PassengerResponseDto::getFlightId).containsOnly(88L);
        assertThat(list).extracting(PassengerResponseDto::getUserId).containsOnly(10L);
    }
}
