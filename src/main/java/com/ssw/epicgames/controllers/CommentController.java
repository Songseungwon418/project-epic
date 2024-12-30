package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.CommentEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.services.ArticleService;
import com.ssw.epicgames.services.CommentService;
import com.ssw.epicgames.vos.CommentVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/comment")
public class CommentController {
    private final CommentService commentService;
    private final ArticleService articleService;

    @Autowired
    public CommentController(CommentService commentService, ArticleService articleService) {
        this.commentService = commentService;
        this.articleService = articleService;
    }

    @RequestMapping(value = "/", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchIndex(@RequestParam(value = "index", required = false, defaultValue = "0") int index, @RequestParam(value = "content", required = false) String content) {
        CommonResult result = this.commentService.modifyComment(index, content);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteIndex(@RequestParam(value = "index", required = false, defaultValue = "0") int index) {
        CommonResult result = this.commentService.deleteComment(index);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<CommentVo[]> getComments(@RequestParam(value = "articleIndex", required = false, defaultValue = "0") int articleIndex) {
        CommentVo[] comments = this.commentService.getCommentsByArticleIndex(articleIndex);
        if (comments == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(comments);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postIndex(CommentEntity comment) {
        CommonResult result = this.commentService.writeComment(comment);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());

        if (result == CommonResult.SUCCESS) {
            int commentCount = this.articleService.getCommentCount(comment.getArticleIndex());
            response.put("commentCount", commentCount);
        }

        return response.toString();
    }

}
