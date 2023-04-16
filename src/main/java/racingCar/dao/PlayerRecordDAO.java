package racingCar.dao;

import java.sql.Timestamp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import racingCar.domain.RacingCar;

@Repository
public class PlayerRecordDAO {
    private JdbcTemplate jdbcTemplate;

    public PlayerRecordDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insert(int playId, RacingCar car) {
        String sql = "insert into PLAYER_RECORD (play_id, player_name, player_position) values (?, ?, ?)";
        return jdbcTemplate.update(sql, playId, car.getName(), car.getPosition());
    }
}
