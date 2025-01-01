package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.GameDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.exceptions.TransactionalException;
import com.ssw.epicgames.mappers.GameMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    public Result addGame(GameEntity game, MultipartFile[] images, String tag1, String tag2) throws IOException {
        // 데이터 유효성 검사
        if (game == null ||
            tag1 == null || tag2 == null || tag1.isEmpty() || tag2.isEmpty() ||
            images == null || images.length == 0) {
            return CommonResult.FAILURE;
        }

        // 게임 등록
        if(this.gameMapper.insertGame(game) <= 0){
            throw new RuntimeException("게임 등록 실패");
        }
        // 위 삽입 구문이 실행되면 game 객체에 index 값이 자동으로 설정됨
        int gameIndex = game.getIndex();  // game 객체에서 index 값을 가져옴
        // no 값이 잘 설정되었는지 확인
        System.out.println("생성된 game index: " + gameIndex);

        // 게임 이미지들의 유효성 추가 검사 및 바이트형식으로 변환 후 db에 삽입
        for (MultipartFile image : images) {
            // 데이터 유효성 검사(크기 및 확장자)
            if (image.getSize() > 10485760) { // 10MB 크기 제한
                throw new IOException("파일 크기가 너무 큽니다.");
            }
            String filename = image.getOriginalFilename();
            if (filename != null) {
                String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
                // 이미지, 동영상 형식인지 검사
                if (!Arrays.asList("jpg", "jpeg", "png", "gif", "mp4", "avi", "mov", "mkv").contains(extension)) {
                    throw new IOException("유효하지 않은 파일 형식입니다.");
                }
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
        // 받아온 태그들을 리스트로 묶기
        List<String> tags = Arrays.asList(tag1, tag2);
        // 배치 처리로 한 번에 삽입
        if (this.gameMapper.insertGameGenreMapping(gameIndex, tags) <= 0) {
            throw new TransactionalException("게임과 분류 맵핑 실패");
        }
        //endregion

        return CommonResult.SUCCESS;
    }
}
