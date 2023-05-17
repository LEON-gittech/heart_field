CREATE TABLE visitor (
  id int PRIMARY KEY auto_increment,
  is_disabled tinyint DEFAULT 0 NOT NULL COMMENT '0未封禁（有效） 1封禁（⽆效）',
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  username varchar(30) NOT NULL COMMENT '微信名',
  name varchar(15),
  phone varchar(12),
  emergency_name varchar(15),
  emergency_phone varchar(12),
  avatar varchar(200) DEFAULT NULL COMMENT '头像url存储，可空',
  direction varchar(200),
  puzzle varchar(200) COMMENT '最大的困扰',
  history varchar(200) COMMENT '过去事件',
  question varchar(20) COMMENT '状态选择情况 eg [0,1,1,0]表示每个问题选是或否'
);

CREATE TABLE admin (
  id int PRIMARY KEY AUTO_INCREMENT,
  username varchar(30) NOT NULL,
  phone varchar(12) NOT NULL,
  password varchar(36) NOT NULL,
  avatar varchar(200) DEFAULT NULL COMMENT '头像url存储，可空',
  create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_disabled tinyint DEFAULT 0 COMMENT '0未删除 1已删除'
);

CREATE TABLE supervisor (
  id int PRIMARY KEY AUTO_INCREMENT COMMENT '主键，用于唯一标识每个督导',
  is_online tinyint DEFAULT 0 COMMENT '标记督导是否在线，0表示不在线，1表示在线。根据排班信息得到',
  is_disabled tinyint DEFAULT 0 COMMENT '标记督导是否被封禁，0表示未封禁（有效），1表示已封禁（无效）',
  is_valid tinyint DEFAULT 0 COMMENT '标记督导个人信息是否为有效信息，0表示无效，1表示有效',
  create_time timestamp DEFAULT current_timestamp COMMENT '记录督导信息创建时间的时间戳，设置默认值为当前时间',
  update_time timestamp DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '记录最后一次被修改的时间的时间戳，设置默认值为当前时间',
  name varchar(15) COMMENT '督导的真实姓名，长度不超过15个字符',
  age int COMMENT '督导的年龄',
  gender tinyint DEFAULT 2 COMMENT '督导的性别，0表示女性，1表示男性，2表示未知',
  password varchar(36) NOT NULL COMMENT '督导的登录密码，长度不超过36个字符，不能为空',
  avatar varchar(200) COMMENT '督导的头像图片链接，可为空',
  max_concurrent int DEFAULT 2 COMMENT '督导最多同时可以咨询的用户数，设置默认值为2',
  max_num int DEFAULT 20 COMMENT '督导最多可以咨询的用户总数，设置默认值为20',
  phone varchar(20) COMMENT '督导的电话号码',
  card_id varchar(18) COMMENT '督导的身份证号码',
  email varchar(50) COMMENT '督导的电子邮件地址',
  workplace varchar(100) COMMENT '督导的工作单位',
  title varchar(50) COMMENT '督导的职称',
  qualification varchar(50) COMMENT '督导的督导资质',
  qualification_id varchar(20) COMMENT '督导资质的编号，长度不超过20个字符',
  CHECK (is_valid IN (0,1)) ,
  CHECK (gender IN (0,1,2)) ,
  CONSTRAINT password_nn CHECK (password IS NOT NULL)
) COMMENT 'supervisor表，用于存储督导的信息';

CREATE TABLE consultant (
  id int auto_increment PRIMARY KEY COMMENT '主键，自动递增',
  is_online tinyint DEFAULT 0 COMMENT '标记督导是否在线，0表示不在线，1表示在线。根据排班信息得到',
  is_disabled tinyint DEFAULT 0 COMMENT '标记督导是否被封禁，0表示未封禁（有效），1表示已封禁（无效）',
  is_valid tinyint DEFAULT 0 COMMENT '标记督导个人信息及督导与用户绑定关系是否为有效信息，0表示无效，1表示有效',
  create_time timestamp DEFAULT current_timestamp COMMENT '记录督导信息创建时间的时间戳，设置默认值为当前时间',
  update_time timestamp DEFAULT current_timestamp ON UPDATE current_timestamp COMMENT '记录最后一次被修改的时间的时间戳，设置默认值为当前时间，并在更新时自动修改',
  name varchar(15) COMMENT '督导的真实姓名，长度不超过15个字符',
  age int COMMENT '督导的年龄',
  gender tinyint DEFAULT 2 COMMENT '督导的性别，0表示女性，1表示男性，2表示未知',
  password varchar(36) NOT NULL COMMENT  '督导的登录密码，长度不超过36个字符，不能为空',
  avatar varchar(200) COMMENT '督导的头像图片链接，可为空',
  max_concurrent int DEFAULT 2 COMMENT '督导最多同时可以咨询的用户数，设置默认值为2',
  max_num int DEFAULT 20 COMMENT  '督导最多可以咨询的用户总数，设置默认值为20',
  cur_status int NOT NULL DEFAULT 0 COMMENT '督导的当前状态，0表示空闲，1表示忙碌，2表示满',
  help_num int DEFAULT 0 COMMENT '总帮助用户数，设置默认值为0',
  rating double(8,3) DEFAULT 0.000 COMMENT '该督导的平均得分，设置默认值为0.000',
  phone varchar(12) NOT NULL COMMENT '督导的电话号码，不能为空',
  card_id varchar(20) COMMENT '督导的身份证号码',
  detailed_intro text COMMENT '督导的详细介绍',
  brief_intro varchar(200) COMMENT '督导的一句话介绍',
  workplace varchar(50) COMMENT '督导的工作地点',
  email varchar(36) COMMENT '督导的电子邮件地址',
  title varchar(20) COMMENT '督导的职称',
  field varchar(50) COMMENT '督导擅长的领域，多个领域之间用逗号分隔',
  expertise_tag JSON COMMENT '咨询师的标签,包含id(String)和expertiseName(String)两个属性',
  CONSTRAINT gender_check CHECK (gender IN (0,1,2))
) COMMENT='督导信息表';

CREATE TABLE staff_stat (
  id int auto_increment PRIMARY KEY COMMENT '主键，用于唯一标识每个督导',
  staff_type tinyint COMMENT '职员类型，0表示咨询师，1表示督导',
  staff_id int COMMENT '职员id，是一个外键，引用自咨询师或督导表的id列',
  date varchar(15) COMMENT '日期，格式为"yyyy-MM-dd"',
  is_complete tinyint DEFAULT 0 COMMENT '是否是已完成数据，0表示否，1表示是',
  total_time varchar(6) COMMENT '今日咨询时长，单位为秒，使用varchar存储',
  total_count int DEFAULT 0 COMMENT '今日已咨询数，使用int存储',
  CONSTRAINT staff_type_check CHECK (staff_type IN (0,1))
) COMMENT='督导/咨询师咨询情况统计表';

CREATE TABLE binding (
  id int auto_increment PRIMARY KEY COMMENT '主键，用于唯一标识每个记录',
  consultant_id int NOT NULL COMMENT '咨询师id，是一个外键，引用自咨询师表的id列',
  supervisor_id int NOT NULL COMMENT '督导id，是一个外键，引用自督导表的id列',
  create_time timestamp DEFAULT current_timestamp COMMENT '记录创建时间的时间戳，设置默认值为当前时间',
  update_time timestamp NULL COMMENT '记录最后一次被修改的时间的时间戳，可为空',
  is_deleted tinyint DEFAULT 0 COMMENT '是否删除，0表示未删除，1表示已删除',
  CONSTRAINT fk_consultant FOREIGN KEY (consultant_id) REFERENCES consultant(id),
  CONSTRAINT fk_supervisor FOREIGN KEY (supervisor_id) REFERENCES supervisor(id)
) COMMENT='咨询师督导关系表';

CREATE TABLE chat (
  id int auto_increment PRIMARY KEY COMMENT '主键，自增长',
  from_type tinyint COMMENT '发起聊天的用户类型，0表示访客，1表示咨询师，2表示督导',
  from_id int NOT NULL COMMENT '发起聊天的用户id，是一个外键，引用自对应的访客、咨询师或督导表的id列',
  to_type tinyint COMMENT '接收聊天的用户类型，0表示访客，1表示咨询师，2表示督导',
  to_id int NOT NULL COMMENT '接收聊天的用户id，是一个外键，引用自对应的访客、咨询师或督导表的id列',
  start_time timestamp COMMENT '聊天开始时间',
  end_time timestamp COMMENT '聊天结束时间',
  CONSTRAINT from_type_check CHECK (from_type IN (0,1,2)),
  CONSTRAINT to_type_check CHECK (to_type IN (0,1,2)),
  CONSTRAINT user_check CHECK (from_type!=to_type OR from_id!=to_id)
) COMMENT='聊天记录表';

CREATE TABLE record (
  id int auto_increment PRIMARY KEY COMMENT '记录ID',
  is_deleted tinyint(1) DEFAULT 0 COMMENT '是否删除，0未删除，1已删除',
  create_time timestamp DEFAULT current_timestamp COMMENT '创建时间',
  update_time timestamp NULL COMMENT '更新时间',
  consultant_id int NOT NULL COMMENT '咨询师ID',
  supervisor_id int DEFAULT NULL COMMENT '督导ID',
  visitor_id int NOT NULL COMMENT '访客ID',
  start_time timestamp NOT NULL COMMENT '开始时间',
  involve_time timestamp NULL COMMENT '督导介入时间',
  end_time timestamp NOT NULL COMMENT '结束时间',
  visitor_score tinyint(1) COMMENT '访客对咨询师评分，范围0-5',
  visitor_comment varchar(255) COMMENT '访客对咨询师评价',
  record_type varchar(255) COMMENT '咨询记录文件url，保存留用',
  evaluation varchar(255) COMMENT '咨询师对访客评价',
  consult_type varchar(50) COMMENT '咨询师-判定访客咨询类型，字符串列表',
  FOREIGN KEY (consultant_id) REFERENCES consultant(id),
  FOREIGN KEY (supervisor_id) REFERENCES supervisor(id),
  FOREIGN KEY (visitor_id) REFERENCES visitor(id)
)  COMMENT '咨询记录表';

CREATE TABLE help (
  id int auto_increment PRIMARY KEY COMMENT '记录ID',
  is_deleted tinyint(1) DEFAULT 0 COMMENT '是否删除，0未删除，1已删除',
  create_time timestamp DEFAULT current_timestamp COMMENT '创建时间',
  update_time timestamp NULL COMMENT '更新时间',
  consultant_id int NOT NULL COMMENT '咨询师ID',
  supervisor_id int NOT NULL COMMENT '督导ID',
  start_time timestamp NOT NULL COMMENT '开始时间',
  end_time timestamp NOT NULL COMMENT '结束时间',
  FOREIGN KEY (consultant_id) REFERENCES consultant(id),
  FOREIGN KEY (supervisor_id) REFERENCES supervisor(id)
) COMMENT='咨询记录表';

CREATE TABLE message (
  id int auto_increment PRIMARY KEY COMMENT '消息ID',
  chat_id int NOT NULL COMMENT '聊天ID',
  send_time timestamp NOT NULL COMMENT '发送时间',
  owner tinyint(1) NOT NULL COMMENT '消息所有者类型，0:咨询师发送 1:访客发送 2:督导发送',
  sender_id int NOT NULL COMMENT '发送方ID',
  receiver_id int NOT NULL COMMENT '接收方ID',
  type tinyint(1) NOT NULL COMMENT '消息类型，0:文字 1:图片 2:语音 3:表情 4:聊天记录 5:无法识别',
  content varchar(255) COMMENT '消息内容',
  related_chat int DEFAULT 0 COMMENT '关联聊天ID，当消息类型为''聊天记录''时，存储关联的咨询会话记录ID，其余情况设为0',
  create_time timestamp DEFAULT current_timestamp COMMENT '创建时间',
  update_time timestamp NULL COMMENT '更新时间',
  is_deleted tinyint(1) DEFAULT 0 COMMENT '是否删除，0未删除，1已删除',
  FOREIGN KEY (chat_id) REFERENCES chat(id),
  FOREIGN KEY (sender_id) REFERENCES visitor(id),
  FOREIGN KEY (receiver_id) REFERENCES visitor(id)
) COMMENT='消息表';

CREATE TABLE schedule (
  id int PRIMARY KEY auto_increment COMMENT '排班ID',
  staff_type tinyint(1) not null COMMENT '职员类型，0:咨询师，1:督导',
  staff_id int not null COMMENT '职员ID',
  workday int not null COMMENT '值班情况，0-31的数字，用二进制位表示每天的情况，第1位表示1号，第2位表示2号，依此类推，0表示不值班，1表示值班',
  create_time timestamp DEFAULT current_timestamp COMMENT '创建时间',
  FOREIGN KEY (staff_id) REFERENCES supervisor(id),
  FOREIGN KEY (staff_id) REFERENCES consultant(id)
) COMMENT='排班表';

CREATE TABLE waiting (
  id int PRIMARY KEY auto_increment COMMENT '排队记录ID',
  visitor_id int not null COMMENT '访客ID',
  consultant_id int not null COMMENT '咨询师ID',
  number int not null COMMENT '排队序号，每个咨询师是独立的',
  status tinyint(1) not null COMMENT '排队状态，0:排队中，1:已放弃，2:排队完成进入咨询',
  create_time timestamp DEFAULT current_timestamp COMMENT '创建时间',
  update_time timestamp NULL COMMENT '更新时间',
  FOREIGN KEY (visitor_id) REFERENCES visitor(id),
  FOREIGN KEY (consultant_id) REFERENCES consultant(id)
) COMMENT='排队记录表';

CREATE TABLE user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(50) NOT NULL,
  type TINYINT NOT NULL,
  CONSTRAINT type_check CHECK (type IN (0, 1, 2, 3))
);

