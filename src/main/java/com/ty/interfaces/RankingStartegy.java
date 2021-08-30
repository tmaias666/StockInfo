package com.ty.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RankingStartegy{

    List<Map<String, Object>> getBuyRankingInfo(LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getSellRankingInfo(LocalDate startDate, LocalDate endDate);
}
