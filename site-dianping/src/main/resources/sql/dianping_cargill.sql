
-- 店铺信息表
drop table Dianping_ShopInfo_Cargill;
create table dbo.Dianping_ShopInfo_Cargill (
    shop_id varchar(255) not null,
    shop_name varchar(500),
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
    self_category varchar(50),
    self_category_id varchar(50),
    self_sub_region varchar(50),
    self_sub_region_id varchar(50),
    address varchar(500),
    taste_score varchar(50),
    environment_score varchar(50),
    service_score varchar(50),
    page int,
    total_page int,
    sub_category_id varchar(50) not null,
    category_id varchar(50) not null,
    primary_category_id varchar(50),
    sub_region_id varchar(50) not null,
    region_id varchar(50),
    city_id varchar(50),
    source varchar(50) not null,
    keyword varchar(50) not null,
    version varchar(50) not null,
    update_time datetime,
    insert_time datetime
);

--ALTER TABLE DataCenter.dbo.Dianping_ShopInfo_Cargill ADD CONSTRAINT Dianping_ShopInfo_Cargill_PK PRIMARY KEY (shop_id, sub_category_id, sub_region_id, source, keyword, version);
