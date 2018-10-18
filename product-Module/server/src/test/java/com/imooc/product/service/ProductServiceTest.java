package com.imooc.product.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.product.common.DecreaseStockInput;
import com.imooc.product.common.ProductInfoOutput;
import com.imooc.product.dataobject.ProductInfo;


/**
* 
* @since  2018年9月30日 上午11:55:54
* @author llb 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest  {

	
	@Autowired
	private ProductService productService;

	@Test
	public void findAll() throws Exception {
		List<ProductInfo> list =productService.findAll();
		Assert.assertTrue(list.size()>0);
	}
	
	@Test
	public void findList() throws Exception{
		List <ProductInfoOutput> productInfoLists =
				productService.findList(
						Arrays.asList("157875196366160022","157875227953464068"));
		Assert.assertTrue(productInfoLists.size()>0);
	}
	
	@Test
	public void decreaseStock()  throws Exception{
		DecreaseStockInput cartDTO = new DecreaseStockInput ("157875227953464068",2);
		
		productService.decreaseStock(Arrays.asList(cartDTO));
		
		
		
	}

}
