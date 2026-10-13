package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isDeal = 1 ORDER BY discountPercent DESC")
    fun getDeals(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isBestSeller = 1 ORDER BY rating DESC")
    fun getBestSellers(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isNewArrival = 1 ORDER BY id DESC")
    fun getNewArrivals(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
