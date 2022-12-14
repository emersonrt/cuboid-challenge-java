package co.fullstacklabs.cuboid.challenge.service;

import co.fullstacklabs.cuboid.challenge.dto.CuboidDTO;
import co.fullstacklabs.cuboid.challenge.exception.ResourceNotFoundException;
import co.fullstacklabs.cuboid.challenge.exception.UnprocessableEntityException;
import co.fullstacklabs.cuboid.challenge.model.Bag;
import co.fullstacklabs.cuboid.challenge.model.Cuboid;
import co.fullstacklabs.cuboid.challenge.repository.BagRepository;
import co.fullstacklabs.cuboid.challenge.repository.CuboidRepository;
import co.fullstacklabs.cuboid.challenge.service.impl.CuboidServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import testbuilders.BagTestBuilder;
import testbuilders.CuboidTestBuilder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CuboidServiceTest {

    @InjectMocks
    private CuboidServiceImpl cuboidService;
    @Mock
    private CuboidRepository repository;
    @Mock
    private BagRepository bagRepository;
    @Mock
    private ModelMapper mapper;

    @Test
    void updateWithSuccess() {
        Bag bag = BagTestBuilder.builder().id(1L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().id(1L).width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .id(cuboid.getId())
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();
        Mockito.when(bagRepository.findById(cuboidDTO.getBagId())).thenReturn(Optional.of(bag));
        Mockito.when(repository.findById(cuboidDTO.getId())).thenReturn(Optional.of(cuboid));
        Mockito.when(repository.save(Mockito.any(Cuboid.class))).thenReturn(cuboid);
        Mockito.when(mapper.map(cuboid, CuboidDTO.class)).thenReturn(cuboidDTO);

        cuboidService.update(cuboidDTO.getId(), cuboidDTO);

        ArgumentCaptor<Cuboid> bagCaptor = ArgumentCaptor.forClass(Cuboid.class);
        Mockito.verify(bagRepository).findById(cuboidDTO.getBagId());
        Mockito.verify(repository).findById(cuboidDTO.getId());
        Mockito.verify(repository).save(bagCaptor.capture());
        Mockito.verify(mapper).map(cuboid, CuboidDTO.class);

        assertEquals(cuboid.getHeight(), bagCaptor.getValue().getHeight());
        assertEquals(cuboid.getWidth(), bagCaptor.getValue().getWidth());
        assertEquals(cuboid.getDepth(), bagCaptor.getValue().getDepth());
    }

    @Test
    void updateWithCuboidNotFound() {
        Bag bag = BagTestBuilder.builder().id(1L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().id(9999999999L).width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .id(cuboid.getId())
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();
        Mockito.when(bagRepository.findById(cuboidDTO.getBagId())).thenReturn(Optional.of(bag));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cuboidService.update(cuboidDTO.getId(), cuboidDTO));
    }

    @Test
    void updateWithBagNotFound() {
        Bag bag = BagTestBuilder.builder().id(1L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().id(1L).width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .id(cuboid.getId())
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cuboidService.update(cuboidDTO.getId(), cuboidDTO));
    }

    @Test
    void updateWithBagNotEnoughCapacity() {
        Bag bag = BagTestBuilder.builder().id(1L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().id(1L).width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .id(cuboid.getId()).volume(250d)
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();

        Mockito.when(bagRepository.findById(cuboidDTO.getBagId())).thenReturn(Optional.of(bag));
        Mockito.when(repository.findById(cuboidDTO.getId())).thenReturn(Optional.of(cuboid));

        Assertions.assertThrows(UnprocessableEntityException.class, () -> cuboidService.update(cuboidDTO.getId(), cuboidDTO));

        Mockito.verify(bagRepository).findById(cuboidDTO.getBagId());
        Mockito.verify(repository).findById(cuboidDTO.getId());
    }

    @Test
    void deleteWithSuccess() {
        Bag bag = BagTestBuilder.builder().id(2L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().id(1L).width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .id(cuboid.getId())
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();
        Mockito.when(repository.findById(cuboidDTO.getId())).thenReturn(Optional.of(cuboid));

        cuboidService.delete(cuboidDTO.getId());

        Mockito.verify(repository).findById(cuboidDTO.getId());
        Mockito.verify(repository).delete(cuboid);
    }

    @Test
    void deleteWithErrorNoCuboid() {
        Bag bag = BagTestBuilder.builder().id(2L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().id(1L).width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .id(cuboid.getId())
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();
        Mockito.when(repository.findById(cuboidDTO.getId())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> cuboidService.delete(cuboidDTO.getId()));

        Mockito.verify(repository).findById(cuboidDTO.getId());
    }

    /************************************************************
     *                                                           *
     * DO NOT change the tests BELOW, implement the test ABOVE   *
     *                                                           *
    *************************************************************/
    @Test
    void createWithBagNotEnoughCapacity() {
        Bag bag = BagTestBuilder.builder().id(30L).title("title").volume(20d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();
        Mockito.when(mapper.map(cuboidDTO, Cuboid.class)).thenReturn(cuboid);
        Mockito.when(bagRepository.findById(cuboidDTO.getBagId())).thenReturn(Optional.of(bag));
        Assertions.assertThrows(UnprocessableEntityException.class, () -> cuboidService.create(cuboidDTO));
    }
    @Test
    void createWithSuccess() {
        Bag bag = BagTestBuilder.builder().id(10L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();
        Mockito.when(mapper.map(cuboidDTO, Cuboid.class)).thenReturn(cuboid);
        Mockito.when(bagRepository.findById(cuboidDTO.getBagId())).thenReturn(Optional.of(bag));
        Mockito.when(repository.save(Mockito.any(Cuboid.class))).thenReturn(cuboid);

        cuboidService.create(cuboidDTO);

        ArgumentCaptor<Cuboid> bagCaptor = ArgumentCaptor.forClass(Cuboid.class);
        Mockito.verify(mapper).map(cuboidDTO, Cuboid.class);
        Mockito.verify(bagRepository).findById(cuboidDTO.getBagId());
        Mockito.verify(repository).save(bagCaptor.capture());
        Mockito.verify(mapper).map(cuboid, CuboidDTO.class);

        assertEquals(cuboid.getHeight(), bagCaptor.getValue().getHeight());
        assertEquals(cuboid.getWidth(), bagCaptor.getValue().getWidth());
        assertEquals(cuboid.getDepth(), bagCaptor.getValue().getDepth());
    }

    @Test
    void createWithNoBagFound() {
        Bag bag = BagTestBuilder.builder().id(20L).title("title").volume(200d).build();
        Cuboid cuboid = CuboidTestBuilder.builder().width(2f).height(3f).depth(4f).bag(bag).build();
        CuboidDTO cuboidDTO = CuboidDTO.builder()
                .width(cuboid.getWidth()).height(cuboid.getHeight())
                .depth(cuboid.getDepth()).bagId(bag.getId()).build();
        Mockito.when(bagRepository.findById(cuboidDTO.getBagId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> cuboidService.create(cuboidDTO));
    }

    @Test
    void getAll() {
        Bag bag = BagTestBuilder.builder().id(1L).title("title").volume(200d).build();
        Cuboid cuboid1 = CuboidTestBuilder.builder().id(1L).width(2f).height(5f).depth(5f).bag(bag).build();
        Cuboid cuboid2 = CuboidTestBuilder.builder().id(2L).width(2f).height(10f).depth(4f).bag(bag).build();
        List<Cuboid> cuboidList = List.of(cuboid1, cuboid2);
        Mockito.when(repository.findAll()).thenReturn(cuboidList);

        List<CuboidDTO> cuboidDTOList = cuboidService.getAll();
        Mockito.verify(repository).findAll();
        Mockito.verify(mapper).map(cuboidList.get(0), CuboidDTO.class);
        Mockito.verify(mapper).map(cuboidList.get(1), CuboidDTO.class);
        assertNotNull(cuboidDTOList);
    }

}