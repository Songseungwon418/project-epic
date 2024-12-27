package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.CommentEntity;
import com.ssw.epicgames.mappers.CommentMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.vos.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public CommonResult modifyComment(int index, String content) {
        if (index < 1 || content == null || content.isEmpty() || content.length() > 100) {
            return CommonResult.FAILURE;
        }
        CommentVo dbComment = this.commentMapper.selectCommentByIndex(index);
        if (dbComment == null) {
            return CommonResult.FAILURE;
        }
        if (dbComment.getDeletedAt() != null) {
            return CommonResult.FAILURE;
        }

        dbComment.setContent(content);
        dbComment.setUpdatedAt(LocalDateTime.now());
        return this.commentMapper.updateComment(dbComment) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public CommonResult deleteComment(int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        CommentVo dbComment = this.commentMapper.selectCommentByIndex(index);
        if (dbComment == null) {
            return CommonResult.FAILURE;
        }
        if (dbComment.getDeletedAt() != null) {
            return CommonResult.FAILURE;
        }
        dbComment.setDeletedAt(LocalDateTime.now());
        return this.commentMapper.updateComment(dbComment) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public CommentVo[] getCommentsByArticleIndex(int articleIndex) {
        if (articleIndex < 1) {
            return null;
        }
        return this.commentMapper.selectCommentsByArticleIndex(articleIndex);
    }

    public CommonResult writeComment(CommentEntity comment) {
        if (comment == null) {
            return CommonResult.FAILURE;
        }
        if (comment.getArticleIndex() < 1) {
            return CommonResult.FAILURE;
        }
        if (comment.getCommentIndex() != null && comment.getCommentIndex() < 1) {
            return CommonResult.FAILURE;
        }
        if (comment.getUserEmail() == null || comment.getUserEmail().isEmpty() || comment.getUserEmail().length() > 50) {
            return CommonResult.FAILURE;
        }
        if (comment.getContent() == null || comment.getContent().isEmpty() || comment.getContent().length() > 100) {
            return CommonResult.FAILURE;
        }
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(null);
        comment.setDeletedAt(null);
        return this.commentMapper.insertComment(comment) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
}
