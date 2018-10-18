package com.imooc.order.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.order.client.ProductClient;
import com.imooc.order.dataobject.OrderDetail;
import com.imooc.order.dataobject.OrderMaster;
import com.imooc.order.dataobject.ProductInfo;
import com.imooc.order.dto.CartDTO;
import com.imooc.order.dto.OrderDTO;
import com.imooc.order.enums.OrderStatusEnum;
import com.imooc.order.enums.PayStatusEnum;
import com.imooc.order.repository.OrderDetailRepository;
import com.imooc.order.repository.OrderMasterRepository;
import com.imooc.order.service.OrderService;
import com.imooc.order.utils.KeyUtil;

/**
* 
* @since  2018年10月6日 上午9:20:43
* @author llb 
*/
@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	
	@Autowired
	private OrderMasterRepository orderMasterRepository;

	@Autowired
	private ProductClient productClient;
	
	@Override
	public OrderDTO create(OrderDTO orderDTO) {
		/**
		 * TODO 1：参数检验
		 * TODO 2：查询商品信息
		 * TODO 3：计算总价
		 * TODO 4：扣库存（调用商品服务）  
		 */
		String orderId = KeyUtil.getUniqueKey();
		//查询商品信息
		List<String>   productIdList =  orderDTO.getOrderDetailList().stream()
				.map(OrderDetail::getProductId).collect(Collectors.toList());
		List<ProductInfo> productInfoList = productClient.listProductForOrder(productIdList);
		
		//计算总价
		BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
		for(OrderDetail orderDetail:orderDTO.getOrderDetailList()) {
		
			for(ProductInfo productInfo:productInfoList) {
				if(productInfo.getProductId().equals(orderDetail.getProductId())) {
					//单价*数量
					 orderAmount = productInfo.getProductPrice().
							 multiply(new BigDecimal(orderDetail.getProductQuantity()))
							 .add(orderAmount);
					//设置订单详情
					 BeanUtils.copyProperties(productInfo, orderDetail);
					 orderDetail.setDetailId(KeyUtil.getUniqueKey());
					 orderDetail.setOrderId(orderId);
					 //订单详情入库
					 orderDetailRepository.save(orderDetail);
				}
			}
		}
		
		//扣库存
		List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()
				.map(e -> new CartDTO(e.getProductId(),e.getProductQuantity()))
				.collect(Collectors.toList());
	
		productClient.decreaseStock(cartDTOList);
		
		
		
		//5：订单入库  
		OrderMaster orderMaster =new OrderMaster() ;
		orderDTO.setOrderId(KeyUtil.getUniqueKey());//设置唯一主键
		BeanUtils.copyProperties(orderDTO, orderMaster);
		orderMaster.setOrderAmount(orderAmount); 
		orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());//假设
		orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode()); //假设
		
		
		orderMasterRepository.save(orderMaster);
		
		return orderDTO;
	}

}
