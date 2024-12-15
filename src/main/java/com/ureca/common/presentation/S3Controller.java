package com.ureca.common.presentation;

import com.ureca.common.application.S3Service;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class S3Controller {
    private final S3Service s3Service;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "사진리스트업로드 API", description = "사진 여러개를 한번에 s3에 업로드하는 API.")
    ResponseDto<List<String>> getImages(@RequestPart("imgList") List<MultipartFile> imgList) {
        List<String> list = s3Service.stringUpload(imgList);
        return ResponseUtil.SUCCESS("조회에 성공했습니다.", list);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "사진업로드 API", description = "사진 1개를 s3에 업로드하는 API.")
    ResponseDto<String> getImage(@RequestPart MultipartFile img) {
        String result = s3Service.uploadFileImage(img, "default", img.getOriginalFilename());
        return ResponseUtil.SUCCESS("조회에 성공했습니다.", result);
    }
}
