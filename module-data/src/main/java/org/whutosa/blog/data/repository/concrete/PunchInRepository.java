package org.whutosa.blog.data.repository.concrete;

import org.whutosa.blog.data.entity.PunchIn;
import org.whutosa.blog.data.repository.DataRepository;

import java.util.Optional;

/**
 * @author bobo
 * @date 2021/4/20
 */

public interface PunchInRepository extends DataRepository<PunchIn, Integer> {
    Optional<PunchIn> findPunchInByUserIdAndYearAndMonth(Integer userId, Integer year, Integer month);
}
