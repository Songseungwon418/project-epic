package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.ArticleEntity;
import com.ssw.epicgames.entities.ImageEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.ArticleService;
import com.ssw.epicgames.vos.ArticleVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import java.io.IOException;

@Controller
@RequestMapping(value = "/article")
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "index",required = false, defaultValue = "0") int index) {
        ImageEntity image = this.articleService.getImage(index);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .contentLength(image.getData().length)
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }

    @RequestMapping(value = "/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postImage(@RequestParam(value = "upload") MultipartFile file) throws IOException {
        ImageEntity image = new ImageEntity();
        image.setData(file.getBytes());
        image.setContentType(file.getContentType());
        image.setName(file.getOriginalFilename());
        boolean result = this.articleService.uploadImage(image);
        JSONObject response = new JSONObject();
        if (result) {
            response.put("url", "/article/image?index=" + image.getIndex());
        }
        return response.toString();
    }

    @RequestMapping(value = "/modify", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchModify(ArticleEntity article) {
        CommonResult result = this.articleService.modifyArticle(article);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

    @RequestMapping(value = "/modify", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(@RequestParam(value = "index", required = true, defaultValue = "0") int index,
                                  @SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        ArticleEntity article = this.articleService.getArticle(index);
        if (article != null) {
            modelAndView.addObject("article", article);
        }
        modelAndView.addObject("user", user);
        modelAndView.setViewName("article/modify");
        return modelAndView;
    }


    @RequestMapping(value = "/read", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteRead(@RequestParam(value = "index", required = true, defaultValue = "0") int index) {
        CommonResult result = this.articleService.deleteArticle(index);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

    @RequestMapping(value = "/read", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(@RequestParam(value = "index", required = false, defaultValue = "0") int index, @SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        ArticleVo article = this.articleService.getArticle(index);
        int commentCount = this.articleService.getCommentCount(index);
        ArticleEntity nextArticle = this.articleService.getNextArticle(index);
        ArticleEntity prevArticle = this.articleService.getPrevArticle(index);

        modelAndView.addObject("nextArticle", nextArticle);
        modelAndView.addObject("prevArticle", prevArticle);
        modelAndView.addObject("commentCount", commentCount);
        modelAndView.addObject("article", article);
        modelAndView.addObject("user", user);
        modelAndView.addObject("isLoggedIn", user != null);
        modelAndView.addObject("userEmail", user != null ? user.getEmail() : "");
        if (article != null) {
            this.articleService.increaseArticleView(article);
        }
        modelAndView.setViewName("article/read");
        return modelAndView;
    }

    @RequestMapping(value = "/write", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        if(user == null) {
            modelAndView.setViewName("redirect:/user/");
        }
        modelAndView.addObject("user", user);
        modelAndView.setViewName("article/write");
        return modelAndView;
    }

    @RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private String postWrite(ArticleEntity article) {
        CommonResult result = this.articleService.write(article);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            response.put("index", article.getIndex());
        }
        return response.toString();
    }

}
