-- 点评城市列表
drop table Dianping_CityInfo;
create table dbo.Dianping_CityInfo (
    activeCity int,
    appHotLevel int,
    cityAbbrCode nvarchar(50),
    cityAreaCode nvarchar(50),
    cityEnName nvarchar(255),
    cityId nvarchar(50) not null,
    cityLevel nvarchar(50),
    cityName nvarchar(50),
    cityOrderId nvarchar(50),
    cityPyName nvarchar(50),
    directURL nvarchar(255),
    gLat nvarchar(50),
    gLng nvarchar(50),
    overseasCity int,
    parentCityId nvarchar(50),
    provinceId nvarchar(50),
    provinceName nvarchar(50),
    scenery int,
    standardEnName nvarchar(50),
    tuanGouFlag int,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_CityInfo ADD CONSTRAINT Dianping_CityInfo_PK PRIMARY KEY (cityId);



-- 店铺信息表
drop table Dianping_ShopInfo_Budweiser;
create table dbo.Dianping_ShopInfo_Budweiser (
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
    primary_category_id varchar(50) not null,
    sub_region_id varchar(50) not null,
    region_id varchar(50) not null,
    city_id varchar(50) not null,
    update_time datetime,
    insert_time datetime
);

ALTER TABLE DataCenter.dbo.Dianping_ShopInfo_Budweiser 
ADD CONSTRAINT Dianping_ShopInfo_Budweiser_PK PRIMARY KEY (shop_id, sub_category_id, sub_region_id);
