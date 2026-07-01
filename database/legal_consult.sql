CREATE DATABASE IF NOT EXISTS legal_consult DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE legal_consult;

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS lawyer_update_apply;
DROP TABLE IF EXISTS consult_order;
DROP TABLE IF EXISTS consult_appointment;
DROP TABLE IF EXISTS lawyer_schedule;
DROP TABLE IF EXISTS lawyer_category;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS lawyer;
DROP TABLE IF EXISTS legal_category;
SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE legal_category (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '类别ID',
  name VARCHAR(50) NOT NULL UNIQUE COMMENT '咨询类别名称',
  description VARCHAR(200) COMMENT '类别说明',
  base_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '默认每小时收费',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB;

CREATE TABLE lawyer (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '律师ID',
  name VARCHAR(30) NOT NULL COMMENT '律师姓名',
  gender VARCHAR(10) COMMENT '性别',
  phone VARCHAR(15) NOT NULL COMMENT '联系电话',
  license_no VARCHAR(50) UNIQUE NOT NULL COMMENT '律师执业证号',
  experience_years INT NOT NULL DEFAULT 0 COMMENT '从业年限',
  introduction VARCHAR(500) COMMENT '个人简介',
  avatar_url VARCHAR(200) COMMENT '头像URL',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1可预约 0停用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB;

CREATE TABLE customer (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '客户ID',
  name VARCHAR(30) NOT NULL COMMENT '客户姓名',
  phone VARCHAR(15) NOT NULL COMMENT '联系电话',
  email VARCHAR(80) COMMENT '邮箱',
  address VARCHAR(100) COMMENT '地址',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_customer_phone(phone)
) ENGINE=InnoDB;

CREATE TABLE user (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录用户名',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  role VARCHAR(20) NOT NULL COMMENT 'admin/lawyer/customer',
  ref_id INT NOT NULL DEFAULT 0 COMMENT '关联业务ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_user_role_ref(role,ref_id)
) ENGINE=InnoDB;

CREATE TABLE lawyer_category (
  id INT PRIMARY KEY AUTO_INCREMENT,
  lawyer_id INT NOT NULL,
  category_id INT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  UNIQUE KEY uk_lawyer_category(lawyer_id,category_id),
  CONSTRAINT fk_lc_lawyer FOREIGN KEY(lawyer_id) REFERENCES lawyer(id),
  CONSTRAINT fk_lc_category FOREIGN KEY(category_id) REFERENCES legal_category(id)
) ENGINE=InnoDB;

CREATE TABLE lawyer_schedule (
  id INT PRIMARY KEY AUTO_INCREMENT,
  lawyer_id INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0空闲 1已预约 2停诊',
  remark VARCHAR(200),
  KEY idx_schedule_lawyer_time(lawyer_id,start_time,end_time,status),
  CONSTRAINT fk_schedule_lawyer FOREIGN KEY(lawyer_id) REFERENCES lawyer(id)
) ENGINE=InnoDB;

CREATE TABLE consult_appointment (
  id INT PRIMARY KEY AUTO_INCREMENT,
  appointment_no VARCHAR(32) UNIQUE NOT NULL,
  customer_id INT NOT NULL,
  lawyer_id INT NOT NULL,
  category_id INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0待审核 1通过 2驳回 3完成 4取消',
  admin_id INT,
  audit_time DATETIME,
  audit_remark VARCHAR(200),
  customer_remark VARCHAR(200),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_appointment_lawyer_time(lawyer_id,start_time,end_time,status),
  KEY idx_appointment_customer(customer_id,status),
  CONSTRAINT fk_appointment_customer FOREIGN KEY(customer_id) REFERENCES customer(id),
  CONSTRAINT fk_appointment_lawyer FOREIGN KEY(lawyer_id) REFERENCES lawyer(id),
  CONSTRAINT fk_appointment_category FOREIGN KEY(category_id) REFERENCES legal_category(id)
) ENGINE=InnoDB;

CREATE TABLE consult_order (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(32) UNIQUE NOT NULL,
  appointment_id INT NOT NULL,
  customer_id INT NOT NULL,
  lawyer_id INT NOT NULL,
  category_id INT NOT NULL,
  duration_minutes INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0待审核 1通过 2驳回 3已收费 4取消',
  admin_id INT,
  audit_time DATETIME,
  audit_remark VARCHAR(200),
  remark VARCHAR(200),
  order_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_order_appointment(appointment_id),
  KEY idx_order_lawyer_status(lawyer_id,status),
  KEY idx_order_time(order_time,status),
  CONSTRAINT fk_order_appointment FOREIGN KEY(appointment_id) REFERENCES consult_appointment(id),
  CONSTRAINT fk_order_customer FOREIGN KEY(customer_id) REFERENCES customer(id),
  CONSTRAINT fk_order_lawyer FOREIGN KEY(lawyer_id) REFERENCES lawyer(id),
  CONSTRAINT fk_order_category FOREIGN KEY(category_id) REFERENCES legal_category(id)
) ENGINE=InnoDB;

CREATE TABLE lawyer_update_apply (
  id INT PRIMARY KEY AUTO_INCREMENT,
  lawyer_id INT NOT NULL,
  apply_name VARCHAR(30),
  apply_phone VARCHAR(15),
  apply_introduction VARCHAR(500),
  apply_category_ids VARCHAR(200),
  status TINYINT NOT NULL DEFAULT 0 COMMENT '0待审核 1通过 2驳回',
  admin_id INT,
  audit_time DATETIME,
  audit_remark VARCHAR(200),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_apply_lawyer_status(lawyer_id,status),
  CONSTRAINT fk_apply_lawyer FOREIGN KEY(lawyer_id) REFERENCES lawyer(id)
) ENGINE=InnoDB;

INSERT INTO legal_category(name,description,base_price) VALUES
('婚姻家庭','离婚、财产分割、抚养权等法律咨询',300.00),
('合同纠纷','买卖合同、租赁合同、服务合同等纠纷咨询',400.00),
('劳动争议','劳动合同、薪资、工伤、离职赔偿等咨询',350.00),
('交通事故','交通事故责任、赔偿、保险理赔咨询',300.00),
('知识产权','商标、著作权、专利及商业秘密咨询',500.00),
('公司法务','公司设立、股权、治理及合规咨询',600.00);

INSERT INTO lawyer(name,gender,phone,license_no,experience_years,introduction,status) VALUES
('张文远','男','13800001111','LAW2024001',8,'专注婚姻家庭与合同争议，重视方案的可执行性。',1),
('李清妍','女','13900002222','LAW2024002',5,'擅长劳动争议与交通事故，具有丰富调解经验。',1),
('周思齐','男','13500003333','LAW2024003',11,'长期服务科技企业，专注知识产权与公司法务。',1);

INSERT INTO customer(name,phone,email,address) VALUES
('王小明','13600001111','wang@example.com','北京市海淀区'),
('赵丽','13700002222','zhao@example.com','上海市浦东新区');

INSERT INTO user(username,password,role,ref_id) VALUES
('admin','123456','admin',0),
('zhanglawyer','123456','lawyer',1),
('lilawyer','123456','lawyer',2),
('zhoulawyer','123456','lawyer',3),
('wangming','123456','customer',1),
('zhaoli','123456','customer',2);

INSERT INTO lawyer_category(lawyer_id,category_id) VALUES
(1,1),(1,2),(2,3),(2,4),(3,2),(3,5),(3,6);

INSERT INTO lawyer_schedule(lawyer_id,start_time,end_time,status,remark) VALUES
(1,'2026-07-02 09:00:00','2026-07-02 12:00:00',0,'上午咨询'),
(1,'2026-07-02 14:00:00','2026-07-02 18:00:00',0,'下午咨询'),
(2,'2026-07-02 09:00:00','2026-07-02 17:00:00',0,'全天'),
(3,'2026-07-03 09:00:00','2026-07-03 18:00:00',0,'全天');

INSERT INTO consult_appointment(appointment_no,customer_id,lawyer_id,category_id,start_time,end_time,status,admin_id,audit_time,customer_remark) VALUES
('AP202606280001',1,1,1,'2026-06-28 09:00:00','2026-06-28 10:00:00',3,1,'2026-06-27 10:00:00','咨询离婚财产分割'),
('AP202606300002',2,2,3,'2026-06-30 14:00:00','2026-06-30 15:30:00',1,1,'2026-06-29 11:00:00','劳动合同解除争议');

INSERT INTO consult_order(order_no,appointment_id,customer_id,lawyer_id,category_id,duration_minutes,unit_price,total_amount,status,admin_id,audit_time,remark,order_time) VALUES
('OD202606280001',1,1,1,1,60,300.00,300.00,3,1,'2026-06-28 11:00:00','咨询已完成','2026-06-28 10:05:00');
