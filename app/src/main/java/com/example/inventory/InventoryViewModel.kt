package com.example.inventory

import android.widget.Toast
import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

class InventoryViewModel(val itemDao: ItemDao): ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    //add
    private fun insertItem(item: Item){
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }


    //sell
    private fun updateItem(item :Item){
        viewModelScope.launch {
            itemDao.update(item)
        }
    }
    fun sellItem(item :Item){
        if(item.quantityInStock > 0){
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }



    //delete
    fun deleteItem(item:Item){
        viewModelScope.launch{
            itemDao.delete(item)
        }
    }


    //get
    fun retrieveItem(id:Int):LiveData<Item>{
        return itemDao.getItem(id).asLiveData()
    }

    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }


    //update
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ):Item{
        return Item(itemId,itemName,itemPrice.toDouble(),itemCount.toInt())
    }
    fun updateItem(itemId: Int,
                   itemName: String,
                   itemPrice: String,
                   itemCount: String){
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }
}


class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(InventoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }else{
            throw java.lang.IllegalArgumentException("Unknown viewModel class")
        }
    }
}