# MallSystem API

## REST API

服务地址：`http://127.0.0.1:8080`

## 打包

- Windows：执行 `build.bat`
- Linux/macOS：执行 `chmod +x build.sh && ./build.sh`
- JAR 输出：`target/mall-system-0.0.1-SNAPSHOT.jar`

| 方法 | 地址 | 说明 |
| --- | --- | --- |
| GET | `/api/v1/products?page=0&size=20&keyword=可乐` | 获取在售商品列表 |
| GET | `/api/v1/inventory/{productCode}` | 查询商品库存 |
| POST | `/api/v1/orders` | 创建订单并扣减库存 |
| GET | `/api/v1/orders?page=0&size=20` | 分页查询全部订单 |
| POST | `/api/v1/orders/{orderNo}/refund` | 立即退款并恢复库存 |
| DELETE | `/api/v1/orders/{orderNo}` | 删除订单，未发货订单会回补库存 |
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

订单状态支持：`CREATED`、`PAID`、`SHIPPED`、`CLOSED`、`CANCELLED`。`CREATED`、`PAID` 订单可直接退款，重复退款不会重复恢复库存；服务不会自动发货或自动修改状态。

## MCP

MCP 地址：`http://127.0.0.1:8080/mcp`

提供以下工具：

- `list_products`：获取商品列表
- `get_inventory`：查询库存
- `list_my_orders`：分页查询全部订单
- `create_order`：创建订单
- `update_order_status`：修改订单状态
- `refund_order`：立即退款并恢复库存
- `delete_order`：删除订单并按需恢复库存

其中 `create_order`、`update_order_status`、`refund_order` 和 `delete_order` 必须明确传入 `confirmed=true`。
