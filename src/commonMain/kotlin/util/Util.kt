package util

fun <X,Y,Z> Map<X,Map<Y,Z>>.switchIndexes(): Map<Y,Map<X,Z>> =
    this.flatMap { (id1, map2) ->
            map2.toList().map { (id2, value) -> Triple(id2, id1, value) }
        } // List of triples
        .groupBy { it.first } // Map<id2, List<Triple>
        .mapValues { (_, triples) -> triples.associate { it.second to it.third } }
