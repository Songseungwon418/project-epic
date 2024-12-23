package com.ssw.epicgames.vos;

import com.ssw.epicgames.entities.ArticleEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleVo extends ArticleEntity {
    private String nickname;
    private int commentCount;
}
