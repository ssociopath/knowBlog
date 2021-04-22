package org.whutosa.blog.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author bobo
 * @date 2021/4/21
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchOperationResultDTO<ID> {
    private Integer successCount;
    private List<IdAndReson<ID>> failed;

    @Data
    @AllArgsConstructor
    public static class IdAndReson<ID> {
        private ID entityId;
        private String reason;

        public static <ID> IdAndReson<ID> fromSingleDTO(OperationResultDTO<ID> dto) {
            return new IdAndReson<>(dto.getEntityId(), dto.getFailedReason());
        }
    }

    public static <ID> Collector<OperationResultDTO<ID>, BatchOperationResultDTO<ID>, BatchOperationResultDTO<ID>> toBatchResult() {
        return new Collector<OperationResultDTO<ID>, BatchOperationResultDTO<ID>, BatchOperationResultDTO<ID>>() {
            @Override
            public Supplier<BatchOperationResultDTO<ID>> supplier() {
                return BatchOperationResultDTO::new;
            }

            @Override
            public BiConsumer<BatchOperationResultDTO<ID>, OperationResultDTO<ID>> accumulator() {
                return (batch, single) -> {
                    if (single.getSuccessful()) {
                        batch.setSuccessCount(Optional.ofNullable(batch.getSuccessCount()).orElse(0) + 1);
                    } else {
                        if (null == batch.getFailed()) {
                            batch.setFailed(new LinkedList<>());
                        }
                        batch.getFailed().add(IdAndReson.fromSingleDTO(single));
                    }
                };
            }

            @Override
            public BinaryOperator<BatchOperationResultDTO<ID>> combiner() {
                return (left, right) -> {
                    left.setSuccessCount(left.getSuccessCount() + right.getSuccessCount());
                    left.getFailed().addAll(right.getFailed());
                    return left;
                };
            }

            @Override
            public Function<BatchOperationResultDTO<ID>, BatchOperationResultDTO<ID>> finisher() {
                return dto -> dto;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
            }
        };
    }
}
