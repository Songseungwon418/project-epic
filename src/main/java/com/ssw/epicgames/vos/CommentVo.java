package com.ssw.epicgames.vos;
import com.ssw.epicgames.entities.CommentEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentVo extends CommentEntity {
    private String nickname;
}
