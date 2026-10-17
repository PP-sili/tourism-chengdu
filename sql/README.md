# 数据库脚本说明

## 只需要这一个文件

| 文件 | 说明 |
|------|------|
| **`sql/mysql/02_schema_and_data.sql`** | **最新、唯一需要执行的脚本** |

该文件已包含：

- 创建数据库 `tourism_chengdu`
- 删除并重建全部表（17 张）
- 导入测试数据

已内置的功能：

- 景点评价（`scene_evaluation`，好评/差评/建议）
- 分时段库存（`ticket_stock_slot`，全景点统一 5 段：`08:00-10:00` ~ `16:00-18:00`）
- 门票一人一证（`order_ticket`）
- 商城一单多商品（`order_item`）

> 以前分散的 `01_create_database.sql`、`03_add_scene_evaluation.sql`、`04_migrate_visit_slots.sql` 以及 OpenGauss 版脚本已废弃删除，**不要找那些文件**，只跑本脚本即可。

---

## 怎么执行

### 方式一：Navicat / DataGrip（推荐）

1. 连接 MySQL
2. 打开并运行 **`sql/mysql/02_schema_and_data.sql`**
3. 脚本会自动 `CREATE DATABASE` + `USE tourism_chengdu`

### 方式二：命令行

```bash
mysql -u root -p < sql/mysql/02_schema_and_data.sql
```

---

## 注意

- 脚本会先 **DROP 再 CREATE** 所有表，**会清空库内已有数据**
- 用户表名为 **`app_user`**（避免 MySQL 保留字 `user`）
- 后端 `application.yml` 默认连：`localhost:3306/tourism_chengdu`，账号 `root` / 密码 `root`
- **若登录返回「服务器内部错误」**：多半是 MySQL 密码不对。复制 `application-local.yml.example` 为 `application-local.yml`，填入真实密码后重启后端

---

## 测试账号（密码均为 `123456`）

| 手机号 | 昵称 |
|--------|------|
| 13800001111 | 熊猫爱好者 |
| 13800002222 | 成都老饕 |
| 13800003333 | 青城道长 |
| 13800004444 | 锦里闲人 |

---

## 表清单（17 张）

`app_user` · `sms_record` · `scene` · `banner` · `ticket_stock` · `ticket_stock_slot` · `order` · `order_ticket` · `order_item` · `refund_record` · `scene_evaluation` · `post` · `post_like` · `product` · `cart` · `itinerary` · `stat_daily`
