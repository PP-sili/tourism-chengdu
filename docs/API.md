# API 联调手册

## 1. 基本信息

- **Base URL**：`http://localhost:8080`
- **Content-Type**：`application/json`（上传接口除外）
- **在线文档**：`http://localhost:8080/swagger-ui.html`

## 2. 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

### 错误码

| code | 含义 | 典型场景 |
|------|------|----------|
| 200 | 成功 | 正常返回 |
| 400 | 请求参数错误 | 校验失败、库存不足、状态不允许 |
| 401 | 未登录或登录已过期 | 缺少/无效 JWT |
| 403 | 无权限 | （预留） |
| 404 | 资源不存在 | 订单/景点/商品不存在 |
| 409 | 资源冲突 | （预留） |
| 500 | 服务器内部错误 | 未捕获异常 |

## 3. 认证

### 公开接口（无需 Token）

- `/api/auth/**`
- `/api/health`
- `/api/banners/**`
- `/api/scenes/**`
- `/api/products/**`
- `/api/weather/**`
- `/uploads/**`
- `/v3/api-docs/**`、`/swagger-ui/**`

### 需登录接口

请求头：

```
Authorization: Bearer {token}
```

### 登录示例

```http
POST /api/auth/login
Content-Type: application/json

{
  "phone": "13800001111",
  "password": "123456"
}
```

响应 `data.token` 即为 JWT，Swagger UI 中点击 **Authorize**，填入 `Bearer {token}`。

## 4. 测试账号

| 手机号 | 密码 | 昵称 |
|--------|------|------|
| 13800001111 | 123456 | 熊猫爱好者 |
| 13800002222 | 123456 | 成都老饕 |
| 13800003333 | 123456 | 青城道长 |
| 13800004444 | 123456 | 锦里闲人 |

## 5. 接口清单

### 5.1 系统

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | 健康检查 |

### 5.2 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 注册（手机号+密码+可选昵称） |
| POST | `/api/auth/login` | 登录，返回 JWT |

### 5.3 用户 / 我的

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user/profile` | 个人资料 |
| PUT | `/api/user/profile` | 更新昵称/头像 |
| PUT | `/api/user/avatar` | 更新头像 URL |
| GET | `/api/user/like-stats` | 我的帖子获赞总数 |
| GET | `/api/user/overview` | **我的页汇总**（资料+订单统计+购物车+帖子） |

`overview` 响应示例：

```json
{
  "profile": { "id": 1, "phone": "13800001111", "nickname": "熊猫爱好者", "avatar": "..." },
  "totalLikes": 156,
  "postCount": 1,
  "cartItemCount": 2,
  "ticketOrders": {
    "statusCounts": { "pending_use": 1, "used": 1 },
    "total": 2
  },
  "mallOrders": {
    "statusCounts": { "shipped": 1 },
    "total": 1
  }
}
```

### 5.4 首页 / 景点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/banners` | 轮播图 |
| GET | `/api/scenes` | 景点列表 |
| GET | `/api/scenes/{id}` | 景点详情 |
| GET | `/api/scenes/{id}/visitor-stats` | 近 7 日人流 |
| GET | `/api/scenes/{id}/ticket-stats` | 近 7 日余票 |
| GET | `/api/weather/chengdu` | 成都天气（Redis 缓存 30 分钟） |

> 票价为 0 的景点 `bookable: false`，不可预约。

### 5.5 景点评价

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/scenes/{id}/evaluations?type=all\|good\|bad\|suggestion&page=1&size=10` | 评价列表（公开） |
| GET | `/api/scenes/{id}/evaluation-summary` | 好评/差评/建议数量（公开） |
| POST | `/api/evaluations` | 发表评价（需登录） |
| GET | `/api/evaluations/can-evaluate?sceneId=` | 是否可评价（需登录） |
| GET | `/api/evaluations/mine` | 我的评价（需登录） |

**评价类型**：`good` 好评 / `bad` 差评 / `suggestion` 建议

**规则**：

- 每个用户对每个景点只能评价 **1 次**
- 收费景点：需有已支付门票且游玩日期 ≤ 今天
- 免费景点：登录即可评价
- **与社区帖子评论无关**，帖子仍无评论功能

**发表评价**：

```json
{
  "sceneId": 2,
  "type": "good",
  "content": "非常值得一游",
  "rating": 5
}
```

### 5.6 门票订单

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/scenes/{id}/available-dates` | 可预约日期与余票 |
| POST | `/api/ticket/orders` | 创建订单（多票种+每人身份证） |
| POST | `/api/ticket/orders/{orderNo}/pay` | 模拟支付 |
| GET | `/api/ticket/orders?status=&page=1&size=10` | 订单列表 |
| GET | `/api/ticket/orders/{orderNo}` | 订单详情（含每张票 JSON qr_code） |
| POST | `/api/ticket/orders/{orderNo}/refund` | 退票 |

**门票状态**：`pending_pay` → `pending_use` → `used` / `refunded`

**退票截止**：游玩日前一日 20:00

**时段**：`morning` / `afternoon` / `full_day`

**票种**：`adult` / `student` / `child`

### 5.7 行程

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/itinerary` | 行程列表（按日期分组） |
| PUT | `/api/itinerary/{date}` | 更新排序与备注 |

### 5.8 上传

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/upload/image` | multipart 上传图片（max 5MB） |

返回 URL 以 `/uploads/` 开头，用于头像、帖子图片。

### 5.8 社区

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/posts` | 发帖（最多 9 图，可选 sceneId） |
| GET | `/api/posts?sort=time\|hot&page=1&size=10` | 帖子列表 |
| GET | `/api/posts/mine` | 我的帖子 |
| GET | `/api/posts/{id}` | 详情（浏览量+1，无评论） |
| POST | `/api/posts/{id}/like` | 点赞/取消 |

**热度**：`like_count * 2 + view_count`

### 5.9 商城

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/products?category=&page=1&size=10` | 商品列表 |
| GET | `/api/products/{id}` | 商品详情 |
| GET | `/api/cart` | 购物车 |
| POST | `/api/cart` | 加入购物车 |
| PUT | `/api/cart/{id}` | 修改数量/选中 |
| DELETE | `/api/cart/{id}` | 删除 |
| POST | `/api/mall/orders` | 合并下单 |
| POST | `/api/mall/orders/{orderNo}/pay` | 模拟支付 |
| GET | `/api/mall/orders?status=&page=1&size=10` | 订单列表 |
| GET | `/api/mall/orders/{orderNo}` | 详情+物流 |

**商城状态**：`pending_pay` → `pending_ship` → `shipped` → `received`

物流信息由后台直接改库（无管理端 API）：

```sql
UPDATE `order` SET status='shipped', logistics_no='SF1234567890' WHERE order_no='SC...';
UPDATE `order` SET status='received' WHERE order_no='SC...';
```

## 6. 典型联调流程

### 6.1 登录 → 首页

1. `POST /api/auth/login`
2. `GET /api/banners`
3. `GET /api/scenes`
4. `GET /api/weather/chengdu`

### 6.2 门票主链路

1. `GET /api/scenes/1` 确认 `bookable: true`
2. `GET /api/scenes/1/available-dates`
3. `POST /api/ticket/orders`

```json
{
  "sceneId": 1,
  "visitDate": "2026-07-30",
  "timeSlot": "morning",
  "contactName": "张三",
  "contactPhone": "13800001111",
  "tickets": [
    { "ticketType": "adult", "visitorName": "张三", "idCardNo": "510101199001011234" },
    { "ticketType": "student", "visitorName": "张小明", "idCardNo": "510101201001011235" }
  ]
}
```

4. `POST /api/ticket/orders/{orderNo}/pay`
5. `GET /api/ticket/orders/{orderNo}` 查看 qr_code
6. `GET /api/itinerary` 验证行程自动生成

### 6.3 社区发帖

1. `POST /api/upload/image`（multipart）
2. `POST /api/posts` `{ "content": "...", "images": ["/uploads/..."] }`
3. `POST /api/posts/{id}/like`
4. `GET /api/user/like-stats`

### 6.4 商城购物

1. `GET /api/products`
2. `POST /api/cart` `{ "productId": 1, "quantity": 2 }`
3. `POST /api/mall/orders`

```json
{
  "fromCart": true,
  "receiverName": "熊猫爱好者",
  "receiverPhone": "13800001111",
  "receiverAddress": "成都市武侯区锦里中路88号"
}
```

4. `POST /api/mall/orders/{orderNo}/pay`
5. 数据库改 `shipped` + `logistics_no`
6. `GET /api/mall/orders/{orderNo}` 查看物流

## 7. 自测清单

- [ ] 注册 / 登录 / Token 过期（401）
- [ ] 免费景点不可预约（400）
- [ ] 多票种下单 + 支付 + 3 张独立 qr_code
- [ ] 退票（截止前成功，超时失败）
- [ ] 库存不足时下单/支付失败
- [ ] 行程自动生成 + 手动排序保存
- [ ] 图片上传 + 发帖 + 点赞 + 热度排序
- [ ] 购物车合并下单 + 支付 + 物流展示
- [ ] `GET /api/user/overview` 数据正确
- [ ] Swagger UI 可浏览全部接口

## 8. 分页说明

列表接口通用参数：

- `page`：页码，从 1 开始，默认 1
- `size`：每页条数，默认 10，最大 50

响应 `data` 结构：

```json
{
  "list": [],
  "total": 100,
  "page": 1,
  "size": 10
}
```
