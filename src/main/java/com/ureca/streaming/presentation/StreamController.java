package com.ureca.streaming.presentation;

import com.ureca.monitoring.domain.Process;
import com.ureca.monitoring.infrastructure.ProcessRepository;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.streaming.application.IVSService;
import com.ureca.streaming.domain.BroadcastChannelInfo;
import com.ureca.streaming.domain.PlaybackChannelInfo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AWS IVS 채널을 생성하고 스트림 키와 Playback URL을 반환합니다.
 *
 * @return ChannelInfo 객체로 채널 ARN, 스트림 키, Playback URL을 반환합니다.
 */
@RestController
@RequestMapping("/api/ivs")
@Tag(name = "IVS Channel Management", description = "스트리밍 Amazon IVS 채널 및 스트림키 관리 API")
@RequiredArgsConstructor
public class StreamController {
    private final IVSService ivsService;
    @Autowired private ReservationRepository reservationRepository;

    @Autowired private ProcessRepository processRepository;

    @CrossOrigin(
            origins = {
                "https://www.daeng-ggu.com",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175"
            })
    @PostMapping("/create-channel")
    @Operation(summary = "스트리밍쪽 채널 생성", description = "스트리밍쪽 채널을 생성하고 송출 및 수신 URL을 반환하는 API")
    public ResponseEntity<BroadcastChannelInfo> createChannel(@RequestParam Long reservationId) {

        Reservation reservation =
                reservationRepository
                        .findByReservationId(reservationId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Invalid reservationId: " + reservationId));
        //        String channelSendReq = reservationId+"Sender";

        String channelSendReq = convertReservationToChannelId(reservationId);
        String channelArn = ivsService.createChannel(channelSendReq);
        // 송출용 URL가져오기
        String ingestUrl = ivsService.getRtmpEndpoint(channelArn);
        String playbackUrl = ivsService.getPlaybackUrl(channelArn);
        String newStreamKey = ivsService.resetStreamKey(channelArn);
        //        1ivsService.resetStreamKey(channelArn);
        //           2     String streamKey = ivsService.createStreamKey(channelArn);
        //        String streamKey = ivsService.getExistingStreamKey(channelArn);
        Process process = reservation.getProcess();
        if (process == null) {
            process = Process.builder().channelARN(channelArn).playbackUrl(playbackUrl).build();
        } else {
            process.updateStreamValue(playbackUrl, channelArn);
        }
        processRepository.save(process);

        // Reservation에 Process 연결
        reservation.updateProcess(process);
        reservationRepository.save(reservation);

        BroadcastChannelInfo broadcastChannelInfo =
                new BroadcastChannelInfo(ingestUrl, newStreamKey);

        return ResponseEntity.ok(broadcastChannelInfo);
    }

    @GetMapping("/channel-info")
    @Operation(summary = "생성한 IVS 채널 조회", description = "예약 ID를 기반으로 AWS IVS 채널 정보를 조회하는 API")
    public ResponseEntity<BroadcastChannelInfo> getChannelInfo(@RequestParam Long reservationId) {

        // Reservation 조회
        Reservation reservation =
                reservationRepository
                        .findByReservationId(reservationId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Invalid reservationId: " + reservationId));

        // Process 객체 확인
        Process process = reservation.getProcess();
        if (process == null
                || process.getChannelARN() == null
                || process.getChannelARN().isEmpty()) {
            throw new IllegalStateException(
                    "No valid channelARN associated with this reservation.");
        }

        // 송출 및 스트림 키 정보 가져오기
        String ingestUrl = ivsService.getRtmpEndpoint(process.getChannelARN());
        String streamKey = ivsService.getExistingStreamKey2(process.getChannelARN());

        // BroadcastChannelInfo 생성
        BroadcastChannelInfo broadcastChannelInfo = new BroadcastChannelInfo(ingestUrl, streamKey);

        return ResponseEntity.ok(broadcastChannelInfo);
    }

    // 송출 URL 반환 API
    @CrossOrigin(
            origins = {
                "https://www.daeng-ggu.com",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175"
            })
    @Hidden
    @GetMapping("/stream-url")
    public ResponseEntity<String> getStreamUrl(@RequestParam String channelArn) {
        String streamUrl = ivsService.getRTMPUrl(channelArn);
        return ResponseEntity.ok(streamUrl);
    }

    // 수신 URL 반환 API
    @CrossOrigin(
            origins = {
                "https://www.daeng-ggu.com",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175"
            })
    @GetMapping("/playback-url")
    @Operation(summary = "스트리밍쪽 수신 Playback URL", description = "스트리밍 쪽 수신용 ingest URL 반환 API")
    public ResponseEntity<PlaybackChannelInfo> getPlaybackUrl(@RequestParam Long reservationId) {
        Reservation reservation =
                reservationRepository
                        .findByReservationId(reservationId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Invalid reservationId: " + reservationId));

        /*
           Process가 null이거나 channelARN이 없을 경우 명확한 예외 메시지를 반환합
        */
        Process process = reservation.getProcess();

        if (process == null) {
            throw new IllegalStateException("해당 reservation_id에 매핑된 process가 존재하지 않습니다.");
        }
        if (process.getChannelARN() == null || process.getChannelARN().isEmpty()) {
            throw new IllegalStateException(
                    "No valid channelARN associated with this reservation.");
        }

        String channelARN = process.getChannelARN();
        String playbackUrl = ivsService.getPlaybackUrl(channelARN);
        PlaybackChannelInfo playbackChannelInfo = new PlaybackChannelInfo(playbackUrl);

        return ResponseEntity.ok(playbackChannelInfo);
    }

    @DeleteMapping("/delete-channel")
    @Operation(summary = "스트리밍쪽 채널 삭제 API URL", description = "스트리밍 종료시 호출되는 API")
    public ResponseEntity<String> deleteChannel(@RequestParam Long reservationId) {

        Reservation reservation =
                reservationRepository
                        .findByReservationId(reservationId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Invalid reservationId: " + reservationId));

        // Process 및 Reservation 정보 초기화
        Process process = reservation.getProcess();
        if (process == null || process.getChannelARN() == null) {
            throw new IllegalStateException(
                    "No valid channelARN associated with this reservation.");
        }

        // AWS IVS 채널 삭제
        String channelArn = process.getChannelARN();
        ivsService.resetStreamKey(channelArn);
        ivsService.deleteChannel(channelArn);

        process.updateStreamValue(null, null);
        processRepository.save(process);
        return ResponseEntity.ok("Channel deleted successfully");
    }

    // reservationID를 channelId로 변환하는 메서드

    private String convertReservationToChannelId(Long reservationId) {
        // 예약 ID를 채널 ID로 변환하는 로직 (예: 단순 변환 또는 DB 조회)
        // 예시: "12345" -> "channel-12345"
        return "channel-" + reservationId; // 임의 예시
    }
}
