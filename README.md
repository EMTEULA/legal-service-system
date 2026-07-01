# 法律服务咨询预约系统

基于需求规格说明书和设计书实现的课程项目，采用 Spring MVC + MyBatis + MySQL + Vue 3。

## 目录

- `backend-legal`：后端服务，默认端口 `8080`
- `frontend-legal`：Vue 3 前端，默认端口 `5173`
- `database/legal_consult.sql`：MySQL 建库、建表及演示数据

## 快速启动

1. 在 MySQL 8.0 中执行 `database/legal_consult.sql`。
2. 按需设置数据库环境变量（未设置时使用括号内默认值）：
   - `DB_HOST`（`localhost`）
   - `DB_PORT`（`3306`）
   - `DB_NAME`（`legal_consult`）
   - `DB_USER`（`root`）
   - `DB_PASSWORD`（`123456`）
3. 启动后端：

   ```bash
   cd backend-legal
   mvn spring-boot:run
   ```

4. 启动前端：

   ```bash
   cd frontend-legal
   npm install
   npm run dev
   ```

5. 打开 `http://localhost:5173`。

## 演示账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `123456` |
| 律师 | `zhanglawyer` | `123456` |
| 律师 | `lilawyer` | `123456` |
| 客户 | `wangming` | `123456` |
| 客户 | `zhaoli` | `123456` |

演示密码为明文，仅用于课程演示；正式部署应改为 BCrypt。

## 主要业务

- 管理员：类别与律师维护、预约审核、订单审核、律师资料申请审核、数据统计。
- 律师：空闲时间维护、查看已通过预约、完成咨询、创建收费订单、提交资料修改申请。
- 客户：按类别与时段查找律师、提交预约、查看及取消自己的预约、查看订单。
- 预约使用时间交叉公式进行冲突校验；订单金额由后端按时长与单价计算；多表操作使用事务。

