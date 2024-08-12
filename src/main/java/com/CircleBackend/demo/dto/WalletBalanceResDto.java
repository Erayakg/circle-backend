package com.CircleBackend.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WalletBalanceResDto(
        DataDto data
) {
    public static record DataDto(
            List<TokenBalanceDto> tokenBalances
    ) {}

    public static record TokenBalanceDto(
            TokenDto token,
            String amount,
            String updateDate
    ) {}

    public static record TokenDto(
            String id,
            String blockchain,
            String tokenAddress,
            String standard,
            String name,
            String symbol,
            int decimals,
            boolean isNative,
            String updateDate,
            String createDate
    ) {}
}
