package com.ssw.epicgames.vos;

import com.ssw.epicgames.entities.ReviewEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewVo extends ReviewEntity {
    private String nickname;
}
