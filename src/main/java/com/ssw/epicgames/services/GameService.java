package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.GameDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.exceptions.TransactionalException;
import com.ssw.epicgames.mappers.GameMapper;
import com.ssw.epicgames.mappers.GenreMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.vos.GameVo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class GameService {
    private final GameMapper gameMapper;

    @Autowired
    public GameService(GameMapper gameMapper) {
        this.gameMapper = gameMapper;
    }

    public Integer getPurchaseIndex(UserEntity user, int index) {
        if (user == null) {
            return null;
        }
        PurchaseEntity purchase = this.gameMapper.getPurchaseByEmailAndGameIndex(user.getEmail(), index);

        if (purchase == null || !purchase.getUserEmail().equals(user.getEmail()) || purchase.getGameIndex() != index) {
            return null;
        }
        return purchase.getIndex();
    }

    public GameDTO getGameDetails(int index) {
        if (index < 1) {
            return null;
        }
        GameEntity game = this.gameMapper.selectGameInfoByIndex(index);
        GameRatingEntity gameRating = this.gameMapper.selectGameRatingByIndex(index);
        MediaEntity[] gameMedia= this.gameMapper.selectGameMediaByIndex(index);
        GenreEntity[] gameGenre = this.gameMapper.selectGameGenreByIndex(index);
        LanguageEntity[] gameLanguage = this.gameMapper.selectGameLanguageByIndex(index);
        CategoryEntity[] gameCategory = this.gameMapper.selectGameCategoryByIndex(index);
        AchievementEntity[] gameAchievement = this.gameMapper.selectGameAchievementByIndex(index);

        return new GameDTO(game, gameRating, gameMedia, gameGenre, gameLanguage, gameCategory, gameAchievement);
    }

    public GameVo selectGameByIndex(int index) {
        GameVo[] allGames = this.gameMapper.selectAllGames();
        if (index < 0 || index >= allGames.length) {
            return null;
        }
        return allGames[index];
    }

    public List<GameVo> getOnSaleGames() {
        return gameMapper.selectSaleGameIndex();
    }

    public GameVo[] getAllGames() {
        return this.gameMapper.selectAllGames();
    }

    public GameVo[] getGamesByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = "";
        }
        return this.gameMapper.selectGamesByKeyword(keyword);
    }

    // 게임 index에 해당하는 게임 정보 조회
    public GameEntity getGameByIndex(int index) {
        if (index < 1) {
            return null;
        }
        return this.gameMapper.selectGameInfoByIndex(index);
    }


    /** 게임 등록 */
    @Transactional
    public Result addGame(GameEntity game, List<MultipartFile> images, String tag1, String tag2) throws IOException {
        // 데이터 유효성 검사
        if (game == null ||
            tag1 == null || tag2 == null || tag1.isEmpty() || tag2.isEmpty() ||
            images == null || images.isEmpty()) {
            return CommonResult.FAILURE;
        }

        // 게임 등록
        if(this.gameMapper.insertGame(game) <= 0){
            throw new TransactionalException("게임 등록 실패");
        }
        // 위 삽입 구문이 실행되면 game 객체에 index 값이 자동으로 설정됨
        int gameIndex = game.getIndex();  // game 객체에서 index 값을 가져옴
        // no 값이 잘 설정되었는지 확인
        System.out.println("생성된 game index: " + gameIndex);

        for (MultipartFile image : images) {
            // 각 이미지 데이터 유효성 검사
            if (image.isEmpty()) {
                throw new TransactionalException("이미지가 비어 있습니다.");
            }
            // 이미지 파일의 바이트를 확인
            byte[] imageBytes = image.getBytes();
            if (imageBytes.length == 0) {
                throw new TransactionalException("이미지 바이트 변환 실패");
            }

            // 미디어 객체 생성
            MediaEntity media = new MediaEntity();
            // 이미지를 바이트 형식으로 변환 후 미디어 객체에 설정
            media.setImage(image.getBytes());
            // db 삽입 실행
            if (this.gameMapper.insertGameMedia(media) <= 0) {
                throw new TransactionalException("이미지 삽입 실패");
            }
            // 위 삽입 구문이 실행되면 media 객체에 no 값이 자동으로 설정됨
            int mediaNo = media.getNo();  // media 객체에서 no 값을 가져옴
            // no 값이 잘 설정되었는지 확인
            System.out.println("생성된 media no: " + mediaNo);

            // 위에서 등록된 게임의 index 와 이미지 no를 이용하여 맵핑 테이블에 삽입
            if (this.gameMapper.insertGameMediaMapping(gameIndex, mediaNo) <= 0) {
                throw new TransactionalException("게임과 이미지 맵핑 실패");
            }
        }

        // region 등록된 게임의 분류들을 추가
        List<String> tags = Arrays.asList(tag1, tag2); // 받아온 태그들을 리스트로 묶기
        // 배치 처리로 한 번에 삽입
        if (this.gameMapper.insertGameGenreMapping(gameIndex, tags) <= 0) {
            throw new TransactionalException("게임과 분류 맵핑 실패");
        }
        //endregion
        System.out.println("게임 생성 완료");
        return CommonResult.SUCCESS;
    }
}
