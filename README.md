# MallSystem API

## REST API

服务地址：`http://127.0.0.1:8080`

| 方法 | 地址 | 说明 |
| --- | --- | --- |
| GET | `/api/v1/products?page=0&size=20&keyword=可乐` | 获取在售商品列表 |
| GET | `/api/v1/inventory/{productCode}` | 查询商品库存 |
| POST | `/api/v1/orders` | 创建订单并扣减库存 |
| PUT | `/api/v1/orders/{orderNo}/status` | 修改订单状态 |

下单请求：

```json
{
  "items": [
    {
      "productCode": "COCA-330ML",
      "quantity": 2
    }
  ]
}
```

修改状态请求：

```json
{
  "status": "PAID"
}
```

订单状态支持：`CREATED`、`PAID`、`SHIPPED`、`CLOSED`、`CANCELLED`。库存不足时订单事务会回滚，服务不会自动发货或自动修改状态。

## MCP

MCP 地址：`http://127.0.0.1:8080/mcp`

提供以下工具：

- `list_products`：获取商品列表
- `get_inventory`：查询库存
- `create_order`：创建订单
- `update_order_status`：修改订单状态

其中 `create_order` 和 `update_order_status` 必须明确传入 `confirmed=true`。
