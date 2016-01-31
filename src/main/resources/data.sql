insert into servicemetadata(id, provider_display_name, display_name, image_url) values(1,'Boundless Spatial', 'Boundless OpenGeo Suites', 'http://boundlessgeo.com/wp-content/uploads/2015/10/Boundless-new-web-logo-e1444657868290.png');
insert into services (name, id, description, bindable, metadata_id) values ('OpenGeo', '58A0E4B0-DFC2-4CD8-827B-94F6E49EA7BC', 'OpenGeo Server Basic Profile', true, 1);

-- memory & disk are in mb
insert into planconfigs (id, geoserver_docker_uri, geowebcache_docker_uri, geoserver_memory, geoserver_disk, geoserver_instance, geowebcache_memory, geowebcache_disk, geowebcache_instance) values ('planconfigs1', 'jhankes/centos-gs', null, 1536, 1536, 1, 1536, 1536, 1);
insert into planconfigs (id, geoserver_docker_uri, geowebcache, geoserver_memory, geoserver_disk, geoserver_instance, geowebcache_memory, geowebcache_disk, geowebcache_instance)                                   values ('planconfigs2', 'jhankes/centos-gs', null, 4096, 8192, 4, 2048, 8192, 2);

insert into planconfig_other_attributes (planconfig_other_attrib_id, name, value) values ('planconfigs1', 'someAttributeA', 'someValue1');
insert into planconfig_other_attributes (planconfig_other_attrib_id, name, value) values ('planconfigs1', 'testMemory', '4');

insert into planconfig_other_attributes (planconfig_other_attrib_id, name, value) values ('planconfigs2', 'someAttributeB', 'someValue2');
insert into planconfig_other_attributes (planconfig_other_attrib_id, name, value) values ('planconfigs2', 'testMemory', '10');


insert into planmetadata (id) values(1);
insert into planmetadata (id) values(2);

insert into cost(id, planmetadata_id, unit) values(4, 1, 'MONTHLY');
insert into cost(id, planmetadata_id, unit) values(5, 2, 'WEEKLY');

insert into cost_amounts(cost_amounts_id, value, currency) values(4, 0, 'usd');
insert into cost_amounts(cost_amounts_id, value, currency) values(5, 0, 'usd');

insert into plan_metadata_bullets (plan_metadata_id, bullets) values (1, 'Free, Geo Server Service');
insert into plan_metadata_bullets (plan_metadata_id, bullets) values (2, 'Paid, Premium Geo Server Service');

insert into plans (name, id, description, service_id, planconfig_id, metadata_id, is_free) values ('basic', '1DD7E874-E265-44F1-AD2F-676207016CC3', 'Basic Plan limited to 2GB Memory', '58A0E4B0-DFC2-4CD8-827B-94F6E49EA7BC', 'planconfigs1', 1, 1);
insert into plans (name, id, description, service_id, planconfig_id, metadata_id, is_free) values ('premium', '26847B68-57E6-49E2-BA44-F82DCD7E46AA', 'Premium Plan limited to 4GB Memory', '58A0E4B0-DFC2-4CD8-827B-94F6E49EA7BC', 'planconfigs2', 2, 0);



