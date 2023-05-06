package racingcar.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import racingcar.domain.PlayResult;
import racingcar.web.dto.PlayHistoryDto;
import racingcar.web.dto.PlayRequestDto;
import racingcar.web.dto.PlayResponseDto;
import racingcar.web.service.PlayService;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class PlayControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlayService playService;

    @Test
    void plays_valid() throws Exception {
        PlayRequestDto requestDto = new PlayRequestDto("carA,carB", 3);
        PlayResponseDto responseDto = new PlayResponseDto("carA", List.of(
               new PlayResponseDto.RacingCar("carA", 1),
                new PlayResponseDto.RacingCar("carB", 0)
        ));

        List<PlayResult> playResults = List.of(
                new PlayResult(1, "carA"),
                new PlayResult(0, "carB")
        );
        given(playService.play(new String[]{"carA","carB"}, 3)).willReturn(playResults);
        given(playService.findWinners(playResults)).willReturn(new String[]{"carA"});

        mvc.perform(post("/plays")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void plays_invalid() throws Exception {
        PlayRequestDto requestDto = new PlayRequestDto("", -1);

        given(playService.play(new String[]{""}, -1)).willThrow(new IllegalArgumentException("Error Message"));

        mvc.perform(post("/plays")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error Message"));
    }

    @Test
    void history() throws Exception {
        List<PlayHistoryDto> playHistoryDtoList = List.of(
                new PlayHistoryDto("carA", List.of(
                        new PlayResponseDto.RacingCar("carA", 1),
                        new PlayResponseDto.RacingCar("carB", 0)
                )),
                new PlayHistoryDto("carB", List.of(
                        new PlayResponseDto.RacingCar("carA", 0),
                        new PlayResponseDto.RacingCar("carB", 1)
                ))
        );

        given(playService.history()).willReturn(playHistoryDtoList);

        List<PlayResponseDto> playResponseDtoList = playHistoryDtoList.stream()
                .map(PlayHistoryDto::toPlayResponseDto)
                .collect(Collectors.toList());

        mvc.perform(get("/plays")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(playResponseDtoList)));
    }
}