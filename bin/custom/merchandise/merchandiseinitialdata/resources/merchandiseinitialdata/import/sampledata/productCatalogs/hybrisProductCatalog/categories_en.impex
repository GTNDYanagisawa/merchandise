# ImpEx for Importing Categories into Merchandise Store
   
# Macros / Replacement Parameter definitions
$productCatalog=hybrisProductCatalog
   
$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]
   
$lang=en
   
UPDATE Category;$catalogVersion;code[unique=true];name[lang=$lang];description[lang=$lang]
;;1;Hybris Catalogue;
;;100;Stuff;
;;200;Clothes;
;;210;Shirts;
;;220;Sweats;