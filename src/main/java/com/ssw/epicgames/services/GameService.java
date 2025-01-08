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
            game.getName().length() > 100 || game.getCompany().length() > 50 || game.getMinOs().length() > 50 || game.getMinCpu().length() > 255 || game.getMinRam().length() > 50 || game.getMinStorage().length() > 50 || game.getMinGpu().length() > 255 || game.getRecOs().length() > 50 || game.getRecCpu().length() > 255 || game.getRecRam().length() > 50 || game.getRecStorage().length() > 50 || game.getRecGpu().length() > 255 ||
            tag1 == null || tag2 == null || tag1.isEmpty() || tag2.isEmpty() ||
            images == null || images.isEmpty()) {
            return CommonResult.FAILURE;
        }

        //게임 중복 검사(이름과 회사가 둘 다 일치할 시 삽입 불가)
        GameEntity[] dbGames = this.gameMapper.selectAllGames();
        if (dbGames != null || dbGames.length > 0) {
            for (GameEntity dbgame : dbGames) {
                if(dbgame.getName().equals(game.getName()) || dbgame.equals(game.getCompany())) {
                    return CommonResult.FAILURE_DUPLICATE;
                }
            }
        }

        // 게임 등록
        if(this.gameMapper.insertGame(game) <= 0){
            throw new TransactionalException("게임 등록 실패");
        }
        // 위 삽입 구문이 실행되면 game 객체에 index 값이 자동으로 설정됨
        int gameIndex = game.getIndex();  // game 객체에서 index 값을 가져옴
        // no 값이 잘 설정되었는지 확인
        System.out.println("생성된 game index: " + gameIndex);

        long maxSize = 104857600L; // 100MB (100 * 1024 * 1024)

        for (MultipartFile image : images) {
            // 각 이미지 데이터 유효성 검사
            if (image.isEmpty()) {
                throw new TransactionalException("이미지가 비어 있습니다.");
            }
            // 각 이미지 크기 확인
            else if (image.getSize() > maxSize) {
                throw new TransactionalException("100MB 초과, 이미지 크기를 확인하세요.");
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

        // 언어 매칭 테이블 삽입
        List<Integer> languageIds = this.gameMapper.selectLanguageId(); // 언어테이블에서 언어 id 전체를 가져옴
        // 배치 처리로 한 번에 언어 삽입
        if (this.gameMapper.insertGameLanguageMapping(gameIndex, languageIds) <= 0) {
            throw new TransactionalException("게임과 언어 맵핑 실패");
        }

        // 할인 정보 삽입 - 현재는 게임 인덱스가 홀수면 홀수, 짝수면 짝수로 매칭하는데 나중에 다른 방식으로 수정 필요
        List<Integer> discountsIDs; // 할인 id 담을 리스트, db에서 가져오는 건 방식 바꿀 때 같이 수정
        // 게임 인덱스가 짝수, 홀수 구분
        if (gameIndex % 2 == 0) {
            // 짝수일 경우
            discountsIDs = Arrays.asList(2, 4, 6, 8);
        } else {
            // 홀수일 경우
            discountsIDs= Arrays.asList(1, 3, 5, 7);
        }
        // 할인리스트가 없을 경우
        if (discountsIDs != null && !discountsIDs.isEmpty()) {
            // db에 삽입 시도
            if (this.gameMapper.insertGameDiscountsMapping(gameIndex, discountsIDs) <= 0) {
                throw new TransactionalException("게임과 언어 맵핑 실패");
            }
        }

        System.out.println("게임 생성 완료");
        return CommonResult.SUCCESS;
    }
}
