-- ============================================================
-- 文旅景点导览与票务系统 - 表结构 + 初始化数据（MySQL 8 开发版）
-- Navicat 直接运行本文件即可（已内置建库 + 切库）
-- ============================================================

CREATE DATABASE IF NOT EXISTS tourism_chengdu
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tourism_chengdu;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS order_ticket;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS refund_record;
DROP TABLE IF EXISTS scene_evaluation;
DROP TABLE IF EXISTS post_like;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS itinerary;
DROP TABLE IF EXISTS stat_daily;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS ticket_stock_slot;
DROP TABLE IF EXISTS ticket_stock;
DROP TABLE IF EXISTS banner;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS scene;
DROP TABLE IF EXISTS sms_record;
DROP TABLE IF EXISTS app_user;

-- ============================================================
-- 1. 用户模块（表名 app_user，避免 MySQL 保留字 user 冲突）
-- ============================================================
CREATE TABLE app_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    phone           VARCHAR(11)     NOT NULL COMMENT '手机号（登录账号）',
    nickname        VARCHAR(50)     DEFAULT '蓉城游客' COMMENT '昵称',
    avatar          VARCHAR(255)    DEFAULT NULL COMMENT '头像URL',
    password        VARCHAR(255)    NOT NULL COMMENT 'BCrypt 加密密码',
    status          TINYINT         DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    last_login_time TIMESTAMP       NULL DEFAULT NULL COMMENT '最后登录时间',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE sms_record (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    phone       VARCHAR(11) NOT NULL COMMENT '手机号',
    code        VARCHAR(6)  NOT NULL COMMENT '验证码',
    type        TINYINT     DEFAULT 1 COMMENT '类型：1-登录 2-注册 3-找回密码',
    status      TINYINT     DEFAULT 0 COMMENT '状态：0-未使用 1-已使用',
    expire_time TIMESTAMP   NOT NULL COMMENT '过期时间',
    create_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_sms_record_phone (phone),
    KEY idx_sms_record_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码记录表（第一版不使用，预留）';

-- ============================================================
-- 2. 景点模块
-- ============================================================
CREATE TABLE scene (
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '景点ID',
    name                VARCHAR(100)    NOT NULL COMMENT '景点名称',
    subtitle            VARCHAR(200)    DEFAULT NULL COMMENT '副标题',
    cover_image         VARCHAR(255)    DEFAULT NULL COMMENT '封面图URL',
    images              TEXT            DEFAULT NULL COMMENT '详情轮播图JSON数组',
    history_text        LONGTEXT        DEFAULT NULL COMMENT '历史介绍',
    address             VARCHAR(255)    DEFAULT NULL COMMENT '地址',
    city                VARCHAR(50)     DEFAULT '成都' COMMENT '城市',
    ticket_price        DECIMAL(10, 2)  NOT NULL COMMENT '成人票价（0=免费仅展示）',
    student_price       DECIMAL(10, 2)  DEFAULT NULL COMMENT '学生票价',
    child_price         DECIMAL(10, 2)  DEFAULT NULL COMMENT '儿童票价',
    open_time           VARCHAR(50)     DEFAULT '09:00-17:00' COMMENT '开放时间',
    suggested_duration  INT             DEFAULT 120 COMMENT '建议游玩时长（分钟）',
    contact_phone       VARCHAR(20)     DEFAULT NULL COMMENT '联系电话',
    daily_limit         INT             DEFAULT 1000 COMMENT '每日最大售票数',
    status              TINYINT         DEFAULT 1 COMMENT '0-关闭 1-开放',
    sort_order          INT             DEFAULT 0 COMMENT '排序',
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_scene_city (city),
    KEY idx_scene_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';

CREATE TABLE banner (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
    scene_id    BIGINT          DEFAULT NULL COMMENT '关联景点ID',
    image_url   VARCHAR(255)    NOT NULL COMMENT '图片URL',
    title       VARCHAR(100)    DEFAULT NULL COMMENT '标题',
    sub_title   VARCHAR(200)    DEFAULT NULL COMMENT '副标题',
    sort_order  INT             DEFAULT 0 COMMENT '排序',
    status      TINYINT         DEFAULT 1 COMMENT '0-隐藏 1-显示',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';

CREATE TABLE ticket_stock (
    id              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '库存ID',
    scene_id        BIGINT      NOT NULL COMMENT '景点ID',
    stock_date      DATE        NOT NULL COMMENT '日期',
    total_limit     INT         DEFAULT 1000 COMMENT '当日总限额',
    create_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ticket_stock_scene_date (scene_id, stock_date),
    KEY idx_ticket_stock_date (stock_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日票务库存汇总表';

CREATE TABLE ticket_stock_slot (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '时段库存ID',
    scene_id        BIGINT          NOT NULL COMMENT '景点ID',
    stock_date      DATE            NOT NULL COMMENT '日期',
    slot_code       VARCHAR(20)     NOT NULL COMMENT '时段编码，如 08:00-10:00',
    remaining       INT             DEFAULT 0 COMMENT '剩余票数',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ticket_stock_slot (scene_id, stock_date, slot_code),
    KEY idx_ticket_stock_slot_date (stock_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日分时段票务库存表（全景点统一时段）';

-- ============================================================
-- 3. 订单模块
-- ============================================================
CREATE TABLE `order` (
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no            VARCHAR(32)     NOT NULL COMMENT '订单编号',
    user_id             BIGINT          NOT NULL COMMENT '用户ID',
    order_type          TINYINT         NOT NULL COMMENT '1-门票 2-商城',
    scene_id            BIGINT          DEFAULT NULL COMMENT '景点ID',
    scene_name          VARCHAR(100)    DEFAULT NULL COMMENT '景点名称',
    visit_date          DATE            DEFAULT NULL COMMENT '游玩日期',
    visit_time_slot     VARCHAR(20)     DEFAULT NULL COMMENT '游玩时段，如 08:00-10:00',
    ticket_quantity     INT             DEFAULT 0 COMMENT '购票总数',
    total_amount        DECIMAL(10, 2)  NOT NULL COMMENT '订单总金额',
    actual_amount       DECIMAL(10, 2)  NOT NULL COMMENT '实付金额',
    status              VARCHAR(20)     DEFAULT 'pending_pay' COMMENT '订单状态',
    pay_time            TIMESTAMP       NULL DEFAULT NULL COMMENT '支付时间',
    refund_time         TIMESTAMP       NULL DEFAULT NULL COMMENT '退款时间',
    refund_deadline     TIMESTAMP       NULL DEFAULT NULL COMMENT '退票截止时间',
    contact_name        VARCHAR(50)     DEFAULT NULL COMMENT '联系人',
    contact_phone       VARCHAR(11)     DEFAULT NULL COMMENT '联系电话',
    receiver_name       VARCHAR(50)     DEFAULT NULL COMMENT '收货人',
    receiver_phone      VARCHAR(11)     DEFAULT NULL COMMENT '收货电话',
    receiver_address    VARCHAR(255)    DEFAULT NULL COMMENT '收货地址',
    logistics_no        VARCHAR(50)     DEFAULT NULL COMMENT '物流单号',
    remark              VARCHAR(255)    DEFAULT NULL COMMENT '备注',
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_user_id (user_id),
    KEY idx_order_status (status),
    KEY idx_order_visit_date (visit_date),
    KEY idx_order_type (order_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE order_ticket (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '票明细ID',
    order_id        BIGINT          NOT NULL COMMENT '订单ID',
    ticket_type     VARCHAR(20)     NOT NULL COMMENT 'adult/student/child',
    visitor_name    VARCHAR(50)     NOT NULL COMMENT '游客姓名',
    id_card_no      VARCHAR(18)     NOT NULL COMMENT '身份证号',
    unit_price      DECIMAL(10, 2)  NOT NULL COMMENT '单价',
    qr_code         TEXT            DEFAULT NULL COMMENT '单张票二维码JSON',
    status          VARCHAR(20)     DEFAULT 'pending_use' COMMENT '票状态',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_order_ticket_order_id (order_id),
    KEY idx_order_ticket_id_card (id_card_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门票明细表（一人一证一票一码）';

CREATE TABLE order_item (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    order_id        BIGINT          NOT NULL COMMENT '订单ID',
    product_id      BIGINT          NOT NULL COMMENT '商品ID',
    product_name    VARCHAR(100)    DEFAULT NULL COMMENT '商品名称',
    product_price   DECIMAL(10, 2)  NOT NULL COMMENT '单价',
    quantity        INT             NOT NULL DEFAULT 1 COMMENT '数量',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_order_item_order_id (order_id),
    KEY idx_order_item_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单明细表';

CREATE TABLE refund_record (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '退票记录ID',
    order_id        BIGINT          NOT NULL COMMENT '订单ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    scene_id        BIGINT          NOT NULL COMMENT '景点ID',
    scene_name      VARCHAR(100)    DEFAULT NULL COMMENT '景点名称',
    visit_date      DATE            NOT NULL COMMENT '原游玩日期',
    ticket_quantity INT             DEFAULT 0 COMMENT '退票数量',
    refund_amount   DECIMAL(10, 2)  NOT NULL COMMENT '退款金额',
    reason          VARCHAR(255)    DEFAULT NULL COMMENT '退票原因',
    refund_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP COMMENT '退款时间',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_refund_record_order_id (order_id),
    KEY idx_refund_record_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退票记录表';

-- ============================================================
-- 4. 社区模块
-- ============================================================
CREATE TABLE post (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    user_id     BIGINT          NOT NULL COMMENT '发布者ID',
    scene_id    BIGINT          DEFAULT NULL COMMENT '关联景点ID',
    scene_name  VARCHAR(100)    DEFAULT NULL COMMENT '关联景点名称',
    title       VARCHAR(100)    DEFAULT NULL COMMENT '标题',
    content     TEXT            NOT NULL COMMENT '内容',
    images      TEXT            DEFAULT NULL COMMENT '图片JSON数组',
    location    VARCHAR(100)    DEFAULT NULL COMMENT '位置',
    view_count  INT             DEFAULT 0 COMMENT '浏览量',
    like_count  INT             DEFAULT 0 COMMENT '点赞数',
    status      TINYINT         DEFAULT 1 COMMENT '0-隐藏 1-公开',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_post_user_id (user_id),
    KEY idx_post_scene_id (scene_id),
    KEY idx_post_create_time (create_time),
    KEY idx_post_like_count (like_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

CREATE TABLE post_like (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    post_id     BIGINT      NOT NULL COMMENT '帖子ID',
    user_id     BIGINT      NOT NULL COMMENT '用户ID',
    create_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_post_like_post_user (post_id, user_id),
    KEY idx_post_like_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- ============================================================
-- 5. 商城模块
-- ============================================================
CREATE TABLE product (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    name            VARCHAR(100)    NOT NULL COMMENT '商品名称',
    description     TEXT            DEFAULT NULL COMMENT '描述',
    price           DECIMAL(10, 2)  NOT NULL COMMENT '价格',
    original_price  DECIMAL(10, 2)  DEFAULT NULL COMMENT '原价',
    stock           INT             NOT NULL DEFAULT 0 COMMENT '库存',
    sales_count     INT             DEFAULT 0 COMMENT '销量',
    cover_image     VARCHAR(255)    DEFAULT NULL COMMENT '封面图',
    images          TEXT            DEFAULT NULL COMMENT '详情图JSON',
    category        VARCHAR(50)     DEFAULT NULL COMMENT '分类',
    status          TINYINT         DEFAULT 1 COMMENT '0-下架 1-上架',
    sort_order      INT             DEFAULT 0 COMMENT '排序',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_product_status (status),
    KEY idx_product_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE cart (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    product_id      BIGINT          NOT NULL COMMENT '商品ID',
    product_name    VARCHAR(100)    DEFAULT NULL COMMENT '商品名称',
    product_price   DECIMAL(10, 2)  DEFAULT NULL COMMENT '单价',
    quantity        INT             NOT NULL DEFAULT 1 COMMENT '数量',
    selected        TINYINT         DEFAULT 1 COMMENT '是否选中',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cart_user_product (user_id, product_id),
    KEY idx_cart_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 6. 行程模块
-- ============================================================
CREATE TABLE itinerary (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '行程ID',
    user_id     BIGINT      NOT NULL COMMENT '用户ID',
    visit_date  DATE        NOT NULL COMMENT '游玩日期',
    scene_order TEXT        DEFAULT NULL COMMENT '景点ID排序JSON',
    note        TEXT        DEFAULT NULL COMMENT '备注',
    create_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_itinerary_user_date (user_id, visit_date),
    KEY idx_itinerary_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程安排表';

-- ============================================================
-- 7. 数据统计模块
-- ============================================================
CREATE TABLE stat_daily (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    stat_date       DATE            NOT NULL COMMENT '统计日期',
    scene_id        BIGINT          NOT NULL COMMENT '景点ID',
    scene_name      VARCHAR(100)    DEFAULT NULL COMMENT '景点名称',
    visitor_count   INT             DEFAULT 0 COMMENT '客流量',
    ticket_sales    INT             DEFAULT 0 COMMENT '售票数',
    sales_amount    DECIMAL(12, 2)  DEFAULT 0 COMMENT '销售额',
    refund_count    INT             DEFAULT 0 COMMENT '退款订单数',
    refund_amount   DECIMAL(12, 2)  DEFAULT 0 COMMENT '退款金额',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_stat_daily_date_scene (stat_date, scene_id),
    KEY idx_stat_daily_scene_id (scene_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日数据统计表';

-- ============================================================
-- 7. 景点评价模块
-- ============================================================
CREATE TABLE scene_evaluation (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    user_id     BIGINT          NOT NULL COMMENT '用户ID',
    scene_id    BIGINT          NOT NULL COMMENT '景点ID',
    eval_type   VARCHAR(20)     NOT NULL COMMENT '评价类型：good-好评 bad-差评 suggestion-建议',
    content     VARCHAR(500)    NOT NULL COMMENT '评价内容',
    rating      TINYINT         DEFAULT NULL COMMENT '评分1-5，可选',
    status      TINYINT         DEFAULT 1 COMMENT '0-隐藏 1-正常',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_eval_user_scene (user_id, scene_id),
    KEY idx_eval_scene_id (scene_id),
    KEY idx_eval_type (eval_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点评价表';

-- ============================================================
-- 8. 初始化数据（测试密码均为 123456）
-- ============================================================

INSERT INTO app_user (id, phone, nickname, avatar, password, status) VALUES
(1, '13800001111', '熊猫爱好者', NULL, '$2b$10$QzeqXR3S7/V7IDV2psVl2OBMEVAoCITIKzIjdjd/PpROnGZ2.c.ye', 1),
(2, '13800002222', '成都老饕',   NULL, '$2b$10$QzeqXR3S7/V7IDV2psVl2OBMEVAoCITIKzIjdjd/PpROnGZ2.c.ye', 1),
(3, '13800003333', '青城道长',   NULL, '$2b$10$QzeqXR3S7/V7IDV2psVl2OBMEVAoCITIKzIjdjd/PpROnGZ2.c.ye', 1),
(4, '13800004444', '锦里闲人',   NULL, '$2b$10$QzeqXR3S7/V7IDV2psVl2OBMEVAoCITIKzIjdjd/PpROnGZ2.c.ye', 1);

INSERT INTO scene (id, name, subtitle, cover_image, history_text, address, city, ticket_price, student_price, child_price, open_time, suggested_duration, contact_phone, daily_limit, sort_order, status) VALUES
(1,  '青城山',             '青城天下幽，道教发源地之一',           '/images/scene/qingchengshan.jpg',   '青城山位于四川省成都市都江堰市西南，是中国道教名山之一，国家5A级旅游景区。',                     '成都市都江堰市青城山镇',           '成都', 90.00, 45.00, 45.00, '08:00-17:30', 240, '028-87288166', 12000, 1,  1),
(2,  '都江堰水利工程',     '世界文化遗产，千年水利奇迹',           '/images/scene/dujiangyan.jpg',      '都江堰位于四川省成都市都江堰市城西，坐落在成都平原西部的岷江上。',                               '成都市都江堰市公园路',             '成都', 80.00, 40.00, 40.00, '08:00-18:00', 180, '028-87120836', 15000, 2,  1),
(3,  '大熊猫繁育研究基地', '近距离接触国宝大熊猫',               '/images/scene/panda.jpg',           '成都大熊猫繁育研究基地位于成都市成华区外北熊猫大道。',                                             '成都市成华区熊猫大道1375号',       '成都', 55.00, 27.00, 27.00, '07:30-17:00', 180, '028-83510033', 60000, 3,  1),
(4,  '武侯祠',             '中国唯一的君臣合祀祠庙',             '/images/scene/wuhouci.jpg',         '成都武侯祠位于成都市武侯区武侯祠大街。',                                                           '成都市武侯区武侯祠大街231号',     '成都', 50.00, 25.00, 25.00, '08:00-18:00', 120, '028-85552397', 10000, 4,  1),
(5,  '杜甫草堂',           '诗圣杜甫故居，中国文学圣地',         '/images/scene/dufucaotang.jpg',     '杜甫草堂位于成都市青羊区青华路。',                                                                 '成都市青羊区青华路37号',           '成都', 50.00, 25.00, 25.00, '08:00-18:30', 90,  '028-87319258', 8000,  5,  1),
(6,  '宽窄巷子',           '老成都底片，新都市客厅',             '/images/scene/kuanzhaixiangzi.jpg', '宽窄巷子位于成都市青羊区长顺街附近。',                                                             '成都市青羊区宽窄巷子',             '成都',  0.00,  0.00,  0.00, '全天开放',    120, '028-86256918', 20000, 6,  1),
(7,  '锦里古街',           '西蜀第一街，成都版清明上河图',       '/images/scene/jinli.jpg',           '锦里位于成都市武侯区武侯祠大街，毗邻武侯祠。',                                                     '成都市武侯区武侯祠大街231号附2号', '成都',  0.00,  0.00,  0.00, '全天开放',    90,  '028-85568888', 15000, 7,  1),
(8,  '金沙遗址博物馆',     '太阳神鸟出土地，古蜀文明之光',       '/images/scene/jinsha.jpg',          '金沙遗址位于成都市青羊区金沙遗址路。',                                                             '成都市青羊区金沙遗址路2号',       '成都', 70.00, 35.00, 35.00, '08:00-18:00', 120, '028-87303522', 8000,  8,  1),
(9,  '三星堆博物馆',       '沉睡数千年，一醒惊天下',             '/images/scene/sanxingdui.jpg',      '三星堆遗址位于四川省广汉市，距成都约40公里。',                                                     '四川省广汉市西安路133号',           '成都', 72.00, 36.00, 36.00, '08:30-18:00', 180, '0830-5651000', 20000, 9,  1),
(10, '人民公园（鹤鸣茶社）', '体验成都慢生活，百年老茶馆',         '/images/scene/renmingongyuan.jpg',  '成都人民公园始建于1911年。',                                                                       '成都市青羊区少城路12号',           '成都',  0.00,  0.00,  0.00, '06:00-22:00', 60,  '028-86131080', 5000,  10, 1);

INSERT INTO banner (scene_id, image_url, title, sub_title, sort_order, status) VALUES
(3, '/images/banner/banner_panda.jpg',        '萌萌大熊猫',   '来成都看国宝，感受自然之美',       1, 1),
(2, '/images/banner/banner_dujiangyan.jpg',   '千年都江堰',   '世界文化遗产，古人智慧结晶',       2, 1),
(9, '/images/banner/banner_sanxingdui.jpg',   '三星堆探秘',   '沉睡数千年，一醒惊天下',           3, 1),
(1, '/images/banner/banner_qingchengshan.jpg','青城天下幽',  '问道青城山，感受道教文化',         4, 1),
(6, '/images/banner/banner_kuanzhai.jpg',     '宽窄巷子',     '老成都底片，新都市客厅',           5, 1);

INSERT INTO ticket_stock (scene_id, stock_date, total_limit) VALUES
(1, '2026-07-27', 12000),
(1, '2026-07-28', 12000),
(1, '2026-07-29', 12000),
(1, '2026-07-30', 12000),
(1, '2026-07-31', 12000),
(1, '2026-08-01', 12000),
(1, '2026-08-02', 12000),
(2, '2026-07-27', 15000),
(2, '2026-07-28', 15000),
(2, '2026-07-29', 15000),
(2, '2026-07-30', 15000),
(2, '2026-07-31', 15000),
(2, '2026-08-01', 15000),
(2, '2026-08-02', 15000),
(3, '2026-07-27', 60000),
(3, '2026-07-28', 60000),
(3, '2026-07-29', 60000),
(3, '2026-07-30', 60000),
(3, '2026-07-31', 60000),
(3, '2026-08-01', 60000),
(3, '2026-08-02', 60000),
(4, '2026-07-27', 10000),
(4, '2026-07-28', 10000),
(4, '2026-07-29', 10000),
(4, '2026-07-30', 10000),
(4, '2026-07-31', 10000),
(4, '2026-08-01', 10000),
(4, '2026-08-02', 10000),
(5, '2026-07-27', 8000),
(5, '2026-07-28', 8000),
(5, '2026-07-29', 8000),
(5, '2026-07-30', 8000),
(5, '2026-07-31', 8000),
(5, '2026-08-01', 8000),
(5, '2026-08-02', 8000),
(8, '2026-07-27', 8000),
(8, '2026-07-28', 8000),
(8, '2026-07-29', 8000),
(8, '2026-07-30', 8000),
(8, '2026-07-31', 8000),
(8, '2026-08-01', 8000),
(8, '2026-08-02', 8000),
(9, '2026-07-27', 20000),
(9, '2026-07-28', 20000),
(9, '2026-07-29', 20000),
(9, '2026-07-30', 20000),
(9, '2026-07-31', 20000),
(9, '2026-08-01', 20000),
(9, '2026-08-02', 20000);

INSERT INTO ticket_stock_slot (scene_id, stock_date, slot_code, remaining) VALUES
(1, '2026-07-27', '08:00-10:00', 300),
(1, '2026-07-27', '10:00-12:00', 300),
(1, '2026-07-27', '12:00-14:00', 300),
(1, '2026-07-27', '14:00-16:00', 300),
(1, '2026-07-27', '16:00-18:00', 800),
(1, '2026-07-28', '08:00-10:00', 350),
(1, '2026-07-28', '10:00-12:00', 350),
(1, '2026-07-28', '12:00-14:00', 350),
(1, '2026-07-28', '14:00-16:00', 350),
(1, '2026-07-28', '16:00-18:00', 900),
(1, '2026-07-29', '08:00-10:00', 325),
(1, '2026-07-29', '10:00-12:00', 325),
(1, '2026-07-29', '12:00-14:00', 325),
(1, '2026-07-29', '14:00-16:00', 325),
(1, '2026-07-29', '16:00-18:00', 850),
(1, '2026-07-30', '08:00-10:00', 275),
(1, '2026-07-30', '10:00-12:00', 275),
(1, '2026-07-30', '12:00-14:00', 275),
(1, '2026-07-30', '14:00-16:00', 275),
(1, '2026-07-30', '16:00-18:00', 750),
(1, '2026-07-31', '08:00-10:00', 200),
(1, '2026-07-31', '10:00-12:00', 200),
(1, '2026-07-31', '12:00-14:00', 200),
(1, '2026-07-31', '14:00-16:00', 200),
(1, '2026-07-31', '16:00-18:00', 600),
(1, '2026-08-01', '08:00-10:00', 400),
(1, '2026-08-01', '10:00-12:00', 400),
(1, '2026-08-01', '12:00-14:00', 400),
(1, '2026-08-01', '14:00-16:00', 400),
(1, '2026-08-01', '16:00-18:00', 1000),
(1, '2026-08-02', '08:00-10:00', 350),
(1, '2026-08-02', '10:00-12:00', 350),
(1, '2026-08-02', '12:00-14:00', 350),
(1, '2026-08-02', '14:00-16:00', 350),
(1, '2026-08-02', '16:00-18:00', 900),
(2, '2026-07-27', '08:00-10:00', 350),
(2, '2026-07-27', '10:00-12:00', 350),
(2, '2026-07-27', '12:00-14:00', 350),
(2, '2026-07-27', '14:00-16:00', 350),
(2, '2026-07-27', '16:00-18:00', 900),
(2, '2026-07-28', '08:00-10:00', 400),
(2, '2026-07-28', '10:00-12:00', 400),
(2, '2026-07-28', '12:00-14:00', 400),
(2, '2026-07-28', '14:00-16:00', 400),
(2, '2026-07-28', '16:00-18:00', 1000),
(2, '2026-07-29', '08:00-10:00', 375),
(2, '2026-07-29', '10:00-12:00', 375),
(2, '2026-07-29', '12:00-14:00', 375),
(2, '2026-07-29', '14:00-16:00', 375),
(2, '2026-07-29', '16:00-18:00', 950),
(2, '2026-07-30', '08:00-10:00', 325),
(2, '2026-07-30', '10:00-12:00', 325),
(2, '2026-07-30', '12:00-14:00', 325),
(2, '2026-07-30', '14:00-16:00', 325),
(2, '2026-07-30', '16:00-18:00', 850),
(2, '2026-07-31', '08:00-10:00', 250),
(2, '2026-07-31', '10:00-12:00', 250),
(2, '2026-07-31', '12:00-14:00', 250),
(2, '2026-07-31', '14:00-16:00', 250),
(2, '2026-07-31', '16:00-18:00', 700),
(2, '2026-08-01', '08:00-10:00', 450),
(2, '2026-08-01', '10:00-12:00', 450),
(2, '2026-08-01', '12:00-14:00', 450),
(2, '2026-08-01', '14:00-16:00', 450),
(2, '2026-08-01', '16:00-18:00', 1200),
(2, '2026-08-02', '08:00-10:00', 400),
(2, '2026-08-02', '10:00-12:00', 400),
(2, '2026-08-02', '12:00-14:00', 400),
(2, '2026-08-02', '14:00-16:00', 400),
(2, '2026-08-02', '16:00-18:00', 1000),
(3, '2026-07-27', '08:00-10:00', 1400),
(3, '2026-07-27', '10:00-12:00', 1400),
(3, '2026-07-27', '12:00-14:00', 1400),
(3, '2026-07-27', '14:00-16:00', 1400),
(3, '2026-07-27', '16:00-18:00', 3800),
(3, '2026-07-28', '08:00-10:00', 1500),
(3, '2026-07-28', '10:00-12:00', 1500),
(3, '2026-07-28', '12:00-14:00', 1500),
(3, '2026-07-28', '14:00-16:00', 1500),
(3, '2026-07-28', '16:00-18:00', 4000),
(3, '2026-07-29', '08:00-10:00', 1350),
(3, '2026-07-29', '10:00-12:00', 1350),
(3, '2026-07-29', '12:00-14:00', 1350),
(3, '2026-07-29', '14:00-16:00', 1350),
(3, '2026-07-29', '16:00-18:00', 3700),
(3, '2026-07-30', '08:00-10:00', 1250),
(3, '2026-07-30', '10:00-12:00', 1250),
(3, '2026-07-30', '12:00-14:00', 1250),
(3, '2026-07-30', '14:00-16:00', 1250),
(3, '2026-07-30', '16:00-18:00', 3500),
(3, '2026-07-31', '08:00-10:00', 1000),
(3, '2026-07-31', '10:00-12:00', 1000),
(3, '2026-07-31', '12:00-14:00', 1000),
(3, '2026-07-31', '14:00-16:00', 1000),
(3, '2026-07-31', '16:00-18:00', 3000),
(3, '2026-08-01', '08:00-10:00', 1500),
(3, '2026-08-01', '10:00-12:00', 1500),
(3, '2026-08-01', '12:00-14:00', 1500),
(3, '2026-08-01', '14:00-16:00', 1500),
(3, '2026-08-01', '16:00-18:00', 4000),
(3, '2026-08-02', '08:00-10:00', 1400),
(3, '2026-08-02', '10:00-12:00', 1400),
(3, '2026-08-02', '12:00-14:00', 1400),
(3, '2026-08-02', '14:00-16:00', 1400),
(3, '2026-08-02', '16:00-18:00', 3800),
(4, '2026-07-27', '08:00-10:00', 250),
(4, '2026-07-27', '10:00-12:00', 250),
(4, '2026-07-27', '12:00-14:00', 250),
(4, '2026-07-27', '14:00-16:00', 250),
(4, '2026-07-27', '16:00-18:00', 700),
(4, '2026-07-28', '08:00-10:00', 275),
(4, '2026-07-28', '10:00-12:00', 275),
(4, '2026-07-28', '12:00-14:00', 275),
(4, '2026-07-28', '14:00-16:00', 275),
(4, '2026-07-28', '16:00-18:00', 750),
(4, '2026-07-29', '08:00-10:00', 225),
(4, '2026-07-29', '10:00-12:00', 225),
(4, '2026-07-29', '12:00-14:00', 225),
(4, '2026-07-29', '14:00-16:00', 225),
(4, '2026-07-29', '16:00-18:00', 650),
(4, '2026-07-30', '08:00-10:00', 200),
(4, '2026-07-30', '10:00-12:00', 200),
(4, '2026-07-30', '12:00-14:00', 200),
(4, '2026-07-30', '14:00-16:00', 200),
(4, '2026-07-30', '16:00-18:00', 600),
(4, '2026-07-31', '08:00-10:00', 150),
(4, '2026-07-31', '10:00-12:00', 150),
(4, '2026-07-31', '12:00-14:00', 150),
(4, '2026-07-31', '14:00-16:00', 150),
(4, '2026-07-31', '16:00-18:00', 500),
(4, '2026-08-01', '08:00-10:00', 300),
(4, '2026-08-01', '10:00-12:00', 300),
(4, '2026-08-01', '12:00-14:00', 300),
(4, '2026-08-01', '14:00-16:00', 300),
(4, '2026-08-01', '16:00-18:00', 800),
(4, '2026-08-02', '08:00-10:00', 250),
(4, '2026-08-02', '10:00-12:00', 250),
(4, '2026-08-02', '12:00-14:00', 250),
(4, '2026-08-02', '14:00-16:00', 250),
(4, '2026-08-02', '16:00-18:00', 700),
(5, '2026-07-27', '08:00-10:00', 200),
(5, '2026-07-27', '10:00-12:00', 200),
(5, '2026-07-27', '12:00-14:00', 200),
(5, '2026-07-27', '14:00-16:00', 200),
(5, '2026-07-27', '16:00-18:00', 500),
(5, '2026-07-28', '08:00-10:00', 225),
(5, '2026-07-28', '10:00-12:00', 225),
(5, '2026-07-28', '12:00-14:00', 225),
(5, '2026-07-28', '14:00-16:00', 225),
(5, '2026-07-28', '16:00-18:00', 550),
(5, '2026-07-29', '08:00-10:00', 190),
(5, '2026-07-29', '10:00-12:00', 190),
(5, '2026-07-29', '12:00-14:00', 190),
(5, '2026-07-29', '14:00-16:00', 190),
(5, '2026-07-29', '16:00-18:00', 480),
(5, '2026-07-30', '08:00-10:00', 175),
(5, '2026-07-30', '10:00-12:00', 175),
(5, '2026-07-30', '12:00-14:00', 175),
(5, '2026-07-30', '14:00-16:00', 175),
(5, '2026-07-30', '16:00-18:00', 450),
(5, '2026-07-31', '08:00-10:00', 125),
(5, '2026-07-31', '10:00-12:00', 125),
(5, '2026-07-31', '12:00-14:00', 125),
(5, '2026-07-31', '14:00-16:00', 125),
(5, '2026-07-31', '16:00-18:00', 350),
(5, '2026-08-01', '08:00-10:00', 250),
(5, '2026-08-01', '10:00-12:00', 250),
(5, '2026-08-01', '12:00-14:00', 250),
(5, '2026-08-01', '14:00-16:00', 250),
(5, '2026-08-01', '16:00-18:00', 600),
(5, '2026-08-02', '08:00-10:00', 225),
(5, '2026-08-02', '10:00-12:00', 225),
(5, '2026-08-02', '12:00-14:00', 225),
(5, '2026-08-02', '14:00-16:00', 225),
(5, '2026-08-02', '16:00-18:00', 550),
(8, '2026-07-27', '08:00-10:00', 200),
(8, '2026-07-27', '10:00-12:00', 200),
(8, '2026-07-27', '12:00-14:00', 200),
(8, '2026-07-27', '14:00-16:00', 200),
(8, '2026-07-27', '16:00-18:00', 500),
(8, '2026-07-28', '08:00-10:00', 225),
(8, '2026-07-28', '10:00-12:00', 225),
(8, '2026-07-28', '12:00-14:00', 225),
(8, '2026-07-28', '14:00-16:00', 225),
(8, '2026-07-28', '16:00-18:00', 550),
(8, '2026-07-29', '08:00-10:00', 190),
(8, '2026-07-29', '10:00-12:00', 190),
(8, '2026-07-29', '12:00-14:00', 190),
(8, '2026-07-29', '14:00-16:00', 190),
(8, '2026-07-29', '16:00-18:00', 480),
(8, '2026-07-30', '08:00-10:00', 175),
(8, '2026-07-30', '10:00-12:00', 175),
(8, '2026-07-30', '12:00-14:00', 175),
(8, '2026-07-30', '14:00-16:00', 175),
(8, '2026-07-30', '16:00-18:00', 450),
(8, '2026-07-31', '08:00-10:00', 125),
(8, '2026-07-31', '10:00-12:00', 125),
(8, '2026-07-31', '12:00-14:00', 125),
(8, '2026-07-31', '14:00-16:00', 125),
(8, '2026-07-31', '16:00-18:00', 350),
(8, '2026-08-01', '08:00-10:00', 250),
(8, '2026-08-01', '10:00-12:00', 250),
(8, '2026-08-01', '12:00-14:00', 250),
(8, '2026-08-01', '14:00-16:00', 250),
(8, '2026-08-01', '16:00-18:00', 600),
(8, '2026-08-02', '08:00-10:00', 225),
(8, '2026-08-02', '10:00-12:00', 225),
(8, '2026-08-02', '12:00-14:00', 225),
(8, '2026-08-02', '14:00-16:00', 225),
(8, '2026-08-02', '16:00-18:00', 550),
(9, '2026-07-27', '08:00-10:00', 350),
(9, '2026-07-27', '10:00-12:00', 350),
(9, '2026-07-27', '12:00-14:00', 350),
(9, '2026-07-27', '14:00-16:00', 350),
(9, '2026-07-27', '16:00-18:00', 900),
(9, '2026-07-28', '08:00-10:00', 375),
(9, '2026-07-28', '10:00-12:00', 375),
(9, '2026-07-28', '12:00-14:00', 375),
(9, '2026-07-28', '14:00-16:00', 375),
(9, '2026-07-28', '16:00-18:00', 950),
(9, '2026-07-29', '08:00-10:00', 340),
(9, '2026-07-29', '10:00-12:00', 340),
(9, '2026-07-29', '12:00-14:00', 340),
(9, '2026-07-29', '14:00-16:00', 340),
(9, '2026-07-29', '16:00-18:00', 880),
(9, '2026-07-30', '08:00-10:00', 300),
(9, '2026-07-30', '10:00-12:00', 300),
(9, '2026-07-30', '12:00-14:00', 300),
(9, '2026-07-30', '14:00-16:00', 300),
(9, '2026-07-30', '16:00-18:00', 800),
(9, '2026-07-31', '08:00-10:00', 250),
(9, '2026-07-31', '10:00-12:00', 250),
(9, '2026-07-31', '12:00-14:00', 250),
(9, '2026-07-31', '14:00-16:00', 250),
(9, '2026-07-31', '16:00-18:00', 700),
(9, '2026-08-01', '08:00-10:00', 400),
(9, '2026-08-01', '10:00-12:00', 400),
(9, '2026-08-01', '12:00-14:00', 400),
(9, '2026-08-01', '14:00-16:00', 400),
(9, '2026-08-01', '16:00-18:00', 1000),
(9, '2026-08-02', '08:00-10:00', 375),
(9, '2026-08-02', '10:00-12:00', 375),
(9, '2026-08-02', '12:00-14:00', 375),
(9, '2026-08-02', '14:00-16:00', 375),
(9, '2026-08-02', '16:00-18:00', 950);

INSERT INTO product (id, name, description, price, original_price, stock, sales_count, cover_image, category, sort_order, status) VALUES
(1,  '成都城市冰箱贴（熊猫款）',     '萌趣熊猫造型，背面带磁铁',                     28.00, 39.00, 500, 128, '/images/product/product_panda_magnet.jpg',    '冰箱贴',   1,  1),
(2,  '成都城市冰箱贴（麻将款）',     '包含"发财""幺鸡"等经典麻将图案',               28.00, 39.00, 400,  89, '/images/product/product_mahjong_magnet.jpg',    '冰箱贴',   2,  1),
(3,  '三星堆青铜面具冰箱贴',         '以青铜纵目面具为原型，复古青铜质感',           35.00, 49.00, 300, 156, '/images/product/product_sanxingdui_magnet.jpg',  '冰箱贴',   3,  1),
(4,  '成都四季明信片（套装12张）',   '包含青城山、都江堰、锦里、宽窄巷子等经典地标', 25.00, 35.00, 600, 235, '/images/product/product_postcard.jpg',          '明信片',   4,  1),
(5,  '成都熊猫公仔（小号）',         '柔软亲肤，高度约25cm',                         68.00, 89.00, 200, 312, '/images/product/product_panda_doll.jpg',        '手工艺品', 5,  1),
(6,  '成都城市帆布包（熊猫主题）',   '印花为熊猫+竹子图案，容量大',                   45.00, 59.00, 350, 167, '/images/product/product_bag.jpg',               '手工艺品', 6,  1),
(7,  '竹叶青茶（明前绿茶）',         '四川峨眉山竹叶青茶，50g罐装',                  88.00,118.00, 150,  89, '/images/product/product_tea.jpg',               '茶叶',     7,  1),
(8,  '成都火锅底料（牛油特辣）',     '正宗牛油特辣口味，500g袋装',                   32.00, 42.00, 800, 456, '/images/product/product_hotpot.jpg',            '火锅底料', 8,  1),
(9,  '熊猫金属书签',                 '镀金工艺，精致小巧',                           18.00, 25.00, 500,  78, '/images/product/product_bookmark.jpg',          '手工艺品', 9,  1),
(10, '成都记忆·手绘地图',            '包含主要景点、美食街、交通攻略',                 15.00, 20.00,1000, 234, '/images/product/product_map.jpg',               '明信片',   10, 1);

INSERT INTO stat_daily (stat_date, scene_id, scene_name, visitor_count, ticket_sales, sales_amount, refund_count, refund_amount) VALUES
('2026-07-20', 1, '青城山', 5800, 6200, 558000.00, 120, 10800.00),
('2026-07-21', 1, '青城山', 6100, 6500, 585000.00,  85,  7650.00),
('2026-07-22', 1, '青城山', 5400, 5800, 522000.00,  95,  8550.00),
('2026-07-23', 1, '青城山', 6200, 6600, 594000.00, 110,  9900.00),
('2026-07-24', 1, '青城山', 7800, 8200, 738000.00, 150, 13500.00),
('2026-07-25', 1, '青城山', 8500, 8900, 801000.00, 180, 16200.00),
('2026-07-26', 1, '青城山', 7200, 7600, 684000.00, 130, 11700.00),
('2026-07-20', 3, '大熊猫繁育研究基地', 22000, 25000, 1375000.00, 300, 16500.00),
('2026-07-21', 3, '大熊猫繁育研究基地', 23500, 26000, 1430000.00, 280, 15400.00),
('2026-07-22', 3, '大熊猫繁育研究基地', 20000, 23000, 1265000.00, 250, 13750.00),
('2026-07-23', 3, '大熊猫繁育研究基地', 24000, 27000, 1485000.00, 320, 17600.00),
('2026-07-24', 3, '大熊猫繁育研究基地', 28000, 31000, 1705000.00, 380, 20900.00),
('2026-07-25', 3, '大熊猫繁育研究基地', 32000, 35000, 1925000.00, 420, 23100.00),
('2026-07-26', 3, '大熊猫繁育研究基地', 26000, 29000, 1595000.00, 350, 19250.00);

INSERT INTO `order` (id, order_no, user_id, order_type, scene_id, scene_name, visit_date, visit_time_slot, ticket_quantity, total_amount, actual_amount, status, pay_time, refund_deadline, contact_name, contact_phone, create_time) VALUES
(1, 'CD202607271001', 1, 1, 1, '青城山',             '2026-07-28', '08:00-10:00', 2, 180.00, 180.00, 'pending_use', '2026-07-27 10:05:00', '2026-07-27 20:00:00', '熊猫爱好者', '13800001111', '2026-07-27 10:00:00'),
(2, 'CD202607271002', 2, 1, 3, '大熊猫繁育研究基地', '2026-07-29', '16:00-18:00', 3, 165.00, 165.00, 'pending_use', '2026-07-27 10:35:00', '2026-07-28 20:00:00', '成都老饕',   '13800002222', '2026-07-27 10:30:00'),
(3, 'CD202607261003', 1, 1, 2, '都江堰水利工程',     '2026-07-27', '12:00-14:00', 1,  80.00,  80.00, 'used',        '2026-07-26 14:05:00', NULL,                  '熊猫爱好者', '13800001111', '2026-07-26 14:00:00'),
(4, 'CD202607261004', 3, 1, 9, '三星堆博物馆',       '2026-07-27', '08:00-10:00', 2, 144.00, 144.00, 'used',        '2026-07-26 09:05:00', NULL,                  '青城道长',   '13800003333', '2026-07-26 09:00:00'),
(5, 'CD202607271005', 4, 1, 4, '武侯祠',             '2026-07-30', '16:00-18:00', 1,  50.00,  50.00, 'pending_pay', NULL,                  '2026-07-29 20:00:00', '锦里闲人',   '13800004444', '2026-07-27 11:00:00');

INSERT INTO order_ticket (order_id, ticket_type, visitor_name, id_card_no, unit_price, qr_code, status) VALUES
(1, 'adult',   '张三',   '510101199001011234', 90.00, '{"ticketId":1,"orderNo":"CD202607271001","sceneId":1,"sceneName":"青城山","visitDate":"2026-07-28","timeSlot":"08:00-10:00","ticketType":"adult","visitorName":"张三","idCardNo":"510101199001011234"}',   'pending_use'),
(1, 'student', '张小明', '510101201001011235', 45.00, '{"ticketId":2,"orderNo":"CD202607271001","sceneId":1,"sceneName":"青城山","visitDate":"2026-07-28","timeSlot":"08:00-10:00","ticketType":"student","visitorName":"张小明","idCardNo":"510101201001011235"}', 'pending_use'),
(2, 'adult',   '李四',   '510101198802022345', 55.00, '{"ticketId":3,"orderNo":"CD202607271002","sceneId":3,"sceneName":"大熊猫繁育研究基地","visitDate":"2026-07-29","timeSlot":"16:00-18:00","ticketType":"adult","visitorName":"李四","idCardNo":"510101198802022345"}', 'pending_use'),
(2, 'adult',   '李妻',   '510101199002022346', 55.00, '{"ticketId":4,"orderNo":"CD202607271002","sceneId":3,"sceneName":"大熊猫繁育研究基地","visitDate":"2026-07-29","timeSlot":"16:00-18:00","ticketType":"adult","visitorName":"李妻","idCardNo":"510101199002022346"}', 'pending_use'),
(2, 'child',   '李娃',   '510101201502022347', 27.00, '{"ticketId":5,"orderNo":"CD202607271002","sceneId":3,"sceneName":"大熊猫繁育研究基地","visitDate":"2026-07-29","timeSlot":"16:00-18:00","ticketType":"child","visitorName":"李娃","idCardNo":"510101201502022347"}', 'pending_use'),
(3, 'adult',   '张三',   '510101199001011234', 80.00, '{"ticketId":6,"orderNo":"CD202607261003","sceneId":2,"sceneName":"都江堰水利工程","visitDate":"2026-07-27","timeSlot":"12:00-14:00","ticketType":"adult","visitorName":"张三","idCardNo":"510101199001011234"}', 'used'),
(4, 'adult',   '王五',   '510101198003033456', 72.00, '{"ticketId":7,"orderNo":"CD202607261004","sceneId":9,"sceneName":"三星堆博物馆","visitDate":"2026-07-27","timeSlot":"08:00-10:00","ticketType":"adult","visitorName":"王五","idCardNo":"510101198003033456"}', 'used'),
(4, 'student', '王小明', '510101201003033457', 36.00, '{"ticketId":8,"orderNo":"CD202607261004","sceneId":9,"sceneName":"三星堆博物馆","visitDate":"2026-07-27","timeSlot":"08:00-10:00","ticketType":"student","visitorName":"王小明","idCardNo":"510101201003033457"}', 'used'),
(5, 'adult',   '赵六',   '510101199504044567', 50.00, NULL, 'pending_use');

INSERT INTO `order` (id, order_no, user_id, order_type, total_amount, actual_amount, status, pay_time, receiver_name, receiver_phone, receiver_address, logistics_no, create_time) VALUES
(6, 'SC202607271001', 1, 2, 81.00, 81.00, 'shipped',  '2026-07-27 15:05:00', '熊猫爱好者', '13800001111', '成都市武侯区锦里中路88号', 'SF1234567890', '2026-07-27 15:00:00'),
(7, 'SC202607271002', 2, 2, 68.00, 68.00, 'received', '2026-07-26 11:05:00', '成都老饕',   '13800002222', '成都市青羊区宽窄巷子12号',  'YT9876543210', '2026-07-26 11:00:00');

INSERT INTO order_item (order_id, product_id, product_name, product_price, quantity) VALUES
(6, 1, '成都城市冰箱贴（熊猫款）',   28.00, 2),
(6, 4, '成都四季明信片（套装12张）', 25.00, 1),
(7, 5, '成都熊猫公仔（小号）',       68.00, 1);

INSERT INTO post (user_id, scene_id, scene_name, title, content, images, location, view_count, like_count, status, create_time) VALUES
(1, 2, '都江堰水利工程',     '都江堰一日游攻略', '都江堰真的是来成都必去的景点之一！', '["/images/post/post1_1.jpg","/images/post/post1_2.jpg"]', '都江堰',   1200, 156, 1, '2026-07-26 20:00:00'),
(2, 3, '大熊猫繁育研究基地', '成都熊猫基地',     '今天终于来看大熊猫啦！',             '["/images/post/post2_1.jpg"]', '熊猫基地', 980, 203, 1, '2026-07-25 18:00:00');

INSERT INTO post_like (post_id, user_id, create_time) VALUES
(1, 2, '2026-07-26 20:30:00'), (1, 3, '2026-07-26 21:00:00'), (2, 1, '2026-07-25 18:30:00');

INSERT INTO scene_evaluation (user_id, scene_id, eval_type, content, rating, status, create_time) VALUES
(1, 2, 'good',       '都江堰水利工程设计精妙，讲解也很专业，非常值得一游！', 5, 1, '2026-07-27 16:00:00'),
(3, 9, 'good',       '三星堆太震撼了，文物保存完好，展陈水平很高。',         5, 1, '2026-07-27 14:30:00'),
(2, 3, 'suggestion', '熊猫基地建议增加遮阳休息区，夏天排队比较晒。',         3, 1, '2026-07-26 17:00:00'),
(4, 6, 'good',       '宽窄巷子很有老成都味道，适合慢慢逛、拍照。',           4, 1, '2026-07-25 19:00:00'),
(2, 2, 'bad',        '节假日人太多，排队进园超过一小时，体验一般。',         2, 1, '2026-07-24 11:00:00');

INSERT INTO cart (user_id, product_id, product_name, product_price, quantity, selected, create_time) VALUES
(1, 1, '成都城市冰箱贴（熊猫款）',   28.00, 2, 1, '2026-07-26 12:00:00'),
(1, 4, '成都四季明信片（套装12张）', 25.00, 1, 1, '2026-07-26 12:30:00');

INSERT INTO itinerary (user_id, visit_date, scene_order, note, create_time, update_time) VALUES
(1, '2026-07-28', '[1,3]', '上午去青城山，下午去熊猫基地。', '2026-07-27 09:00:00', '2026-07-27 09:30:00'),
(2, '2026-07-29', '[3,4]', '先去熊猫基地，下午去武侯祠。',   '2026-07-27 10:00:00', '2026-07-27 10:30:00');

SET FOREIGN_KEY_CHECKS = 1;

-- 重置自增主键（显式插入 id 后执行）
ALTER TABLE app_user AUTO_INCREMENT = 100;
ALTER TABLE scene AUTO_INCREMENT = 100;
ALTER TABLE product AUTO_INCREMENT = 100;
ALTER TABLE `order` AUTO_INCREMENT = 100;

SELECT 'MySQL 数据库 tourism_chengdu 初始化完成，测试密码均为 123456' AS result;
