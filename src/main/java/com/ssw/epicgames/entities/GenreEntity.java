package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"tag"})
public class GenreEntity  {
    private String tag;
    private String name;
}
