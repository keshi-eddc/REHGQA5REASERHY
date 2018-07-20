drop table Dianping_City_SubCategory;
create table dbo.Dianping_City_SubCategory (
    sub_category varchar(255),
    sub_category_id varchar(255) not null,
    category varchar(255),
    category_id varchar(255) not null,
    primary_category varchar(255),
    primary_category_id varchar(255),
    city_id varchar(255) not null,
    city_cnname varchar(255),
    city_enname varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_City_SubCategory ADD CONSTRAINT Dianping_City_SubCategory_PK PRIMARY KEY (sub_category_id,category_id,city_id);

drop table Dianping_HotSearch_Rank;
create table dbo.Dianping_HotSearch_Rank (
    city_cnname varchar(50) not null,
    batch_time varchar(50) not null,
    batch_week_day int,
    batch_week int,
    batch_month int,
    rank int,
    keyword varchar(500) not null,
    search_count int,
    data_type varchar(50),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_HotSearch_Rank 
ADD CONSTRAINT Dianping_HotSearch_Rank_PK PRIMARY KEY (city_cnname, batch_time, keyword, data_type);


drop table Dianping_City_SubRegion;
create table dbo.Dianping_City_SubRegion (
    sub_region varchar(255),
    sub_region_id varchar(255) not null,
    region varchar(255),
    region_id varchar(255) not null,
    city_id varchar(255) not null,
    city_cnname varchar(255),
    city_enname varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_City_SubRegion ADD CONSTRAINT Dianping_City_SubRegion_PK PRIMARY KEY (sub_region_id,region_id,city_id);

drop table Dianping_SubCategory_SubRegion;
create table dbo.Dianping_SubCategory_SubRegion (
    url varchar(500) not null,
    sub_category_id varchar(255) not null,
    sub_category varchar(255),
    category_id varchar(255),
    category varchar(255),
    primary_category_id varchar(255),
    primary_category varchar(255),
    sub_region_id varchar(255) not null,
    sub_region varchar(255) not null,
    region_id varchar(255),
    region varchar(255),
    city_id varchar(255),
    city_cnname varchar(255),
    city_enname varchar(255),
    shop_total_page int,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_SubCategory_SubRegion ADD CONSTRAINT Dianping_SubCategory_SubRegion_PK PRIMARY KEY (url);

drop table Dianping_SubCategory_SubRegion_Page;
create table dbo.Dianping_SubCategory_SubRegion_Page (
    url varchar(500) not null,
    sub_category_id varchar(255) not null,
    sub_category varchar(255),
    category_id varchar(255),
    category varchar(255),
    primary_category_id varchar(255),
    primary_category varchar(255),
    sub_region_id varchar(255) not null,
    sub_region varchar(255) not null,
    region_id varchar(255),
    region varchar(255),
    city_id varchar(255),
    city_cnname varchar(255),
    city_enname varchar(255),
    shop_total_page int,
    page int not null,
    status int,
    version varchar(255) not null,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_SubCategory_SubRegion_Page ADD CONSTRAINT Dianping_SubCategory_SubRegion_Page_PK PRIMARY KEY (url, page, version);

-- 店铺信息表
drop table Dianping_ShopInfo;
create table dbo.Dianping_ShopInfo (
    shop_id varchar(255) not null,
    shop_name varchar(500) not null,
    shop_url varchar(500),
    tuan_support smallint,
    out_support smallint,
    promotion_support smallint,
    book_support smallint,
    has_branch smallint,
    brand_url varchar(255),
    star_level varchar(255),
    review_num int default 0,
    avg_price varchar(50),
    address varchar(500),
    taste_score varchar(50),
    environment_score varchar(50),
    service_score varchar(50),
    sub_category_id varchar(255),
    sub_region_id varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_ShopInfo ADD CONSTRAINT Dianping_ShopInfo_PK PRIMARY KEY (shop_id);

-- 店铺推荐菜-页数
drop table Dianping_Shop_Recommend_Page;
create table dbo.Dianping_Shop_Recommend_Page (
    id varchar(255) not null,
    shop_id varchar(255),
    status int,
    page int,
    total_page int,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_Shop_Recommend_Page ADD CONSTRAINT Dianping_Shop_Recommend_Page_PK PRIMARY KEY (id);

-- 店铺推荐菜
drop table Dianping_Shop_Recommend_Info;
create table dbo.Dianping_Shop_Recommend_Info (
    id varchar(255) not null,
    dish_id varchar(255) not null,
    dish varchar(255),
    dish_url varchar(500),
    dish_image_url varchar(500),
    recommend_tag varchar(500),
    shop_id varchar(255),
    recommend_count int,
    price varchar(255),
    page int,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_Shop_Recommend_Info ADD CONSTRAINT Dianping_Shop_Recommend_Info_PK PRIMARY KEY (id);

-- 店铺推荐菜
drop table Dianping_Shop_Comment_Page;
create table dbo.Dianping_Shop_Comment_Page (
    shop_id varchar(255) not null,
    total_page int not null,
    page int not null,
    status int default -1,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_Shop_Comment_Page ADD CONSTRAINT Dianping_Shop_Comment_Page_PK PRIMARY KEY (shop_id, page);

-- 店铺评论
drop table Dianping_Shop_Comment;
create table dbo.Dianping_Shop_Comment (
    id BIGINT IDENTITY(1,1),
    comment_id varchar(255),
    shop_id varchar(255),
    user_id varchar(255),
    user_name varchar(255),
    user_level varchar(255),
    is_vip int,
    comment_star varchar(255),
    taste_comment varchar(255),
    environment_comment varchar(255),
    service_comment varchar(255),
    avg_price varchar(255),
    comment varchar(max),
    recommend_dish varchar(1000),
    comment_time varchar(1000),
    favorite_num int,
    reply_num int,
    collect_num int,
    page int,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_Shop_Comment ADD CONSTRAINT Dianping_Shop_Comment_PK PRIMARY KEY (id);

-- 用户信息
drop table Dianping_User_Info;
create table dbo.Dianping_User_Info (
    user_id varchar(255) not null,
    user_name varchar(255),
    user_level varchar(255),
    is_vip int,
    sex varchar(20),
    city varchar(100),
    focus_num int,
    fans_num int,
    interaction_num int,
    contribution int,
    community_level varchar(100),
    regist_time varchar(50),
    love_situation varchar(50),
    birthday varchar(50),
    star varchar(50),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_User_Info ADD CONSTRAINT Dianping_User_Info_PK PRIMARY KEY (user_id);

-- 用户签到信息
drop table Dianping_User_Check_Info;
create table dbo.Dianping_User_Check_Info (
    id varchar(255) not null,
    user_id varchar(255),
    user_name varchar(255),
    shop_id varchar(100),
    shop_name varchar(255),
    check_time_str varchar(50),
    check_time varchar(50),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_User_Check_Info ADD CONSTRAINT Dianping_User_Check_Info_PK PRIMARY KEY (id);

-- 店铺详情
drop table Dianping_Shop_Detail_Info;
create table dbo.Dianping_Shop_Detail_Info (
    shop_id varchar(50) not null,
    latitude varchar(50),
    longtitude varchar(255),
    address varchar(500),
    review_num int,
    avg_price varchar(255),
    taste_score varchar(50),
    environment_score varchar(50),
    service_score varchar(50),
    phone varchar(50),
    open_time varchar(255),
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_Shop_Detail_Info ADD CONSTRAINT Dianping_Shop_Detail_Info_PK PRIMARY KEY (shop_id);


