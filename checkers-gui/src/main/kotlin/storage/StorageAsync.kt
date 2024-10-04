package storage

interface StorageAsync<K, T> {
    /** Requires a unique id. */
    suspend fun new(id: K): T
    /** @return null if there is no Entity for given id. */
    suspend fun load(id: K): T?
    /** @throws IllegalArgumentException if there is no entity for that id. */
    suspend fun save(id: K, obj: T)
}
