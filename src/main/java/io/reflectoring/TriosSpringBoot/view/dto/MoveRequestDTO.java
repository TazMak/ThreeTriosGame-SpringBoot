package io.reflectoring.TriosSpringBoot.view.dto;

import io.reflectoring.TriosSpringBoot.model.PlayerColor;
import lombok.Data;

@Data
public class MoveRequestDTO {
    private PlayerColor playerColor;
    private String cardId;
    private CoordinateDTO position;
}