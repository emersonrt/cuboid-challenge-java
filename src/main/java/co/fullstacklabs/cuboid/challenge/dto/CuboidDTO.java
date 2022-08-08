package co.fullstacklabs.cuboid.challenge.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuboidDTO {
    private Long id;

    @NotNull(message = "Cuboid width can't be null.")
    private Float width;

    @NotNull(message = "Cuboid height can't be null.")
    private Float height;

    @NotNull(message = "Cuboid depth can't be null.")
    private Float depth;

    private Double volume;

    @NotNull(message = "Cuboid related bag can't be null.")
    private Long bagId;

    public Double getVolume() {
        Float width = getWidth() == null ? 0 : getWidth();
        Float height = getHeight() == null ? 0 : getHeight();
        Float depth = getDepth() == null ? 0 : getDepth();
        return this.volume == null ? Double.valueOf(width * height * depth): this.volume;
    }
}
