package com.kmkole86.data.common

import com.kmkole86.domain.entity.Range

class RangeUtils {

    companion object {
        ///for the sake of simplicity
        ///just cover ideal case, when ranges overlap
        ///not implemented to cover all the cases
        fun rangeDifference(range1: Range, range2: Range): Range {
            return if (range1.toExclusive < range2.toExclusive)
                Range(fromInclusive = range1.toExclusive, toExclusive = range2.toExclusive)
            else
                Range(fromInclusive = range2.toExclusive, toExclusive = range1.toExclusive);

        }

        fun pageOrdinals(
            range: Range
        ): List<Int> {
            return List(
                range.toExclusive - range.fromInclusive
            ) { index -> range.fromInclusive + index }
        }
    }
}