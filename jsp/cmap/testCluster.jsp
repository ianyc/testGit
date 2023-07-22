<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>大量地標呈現</title>
<script src="GetAPI.html" type=text/javascript></script>
<script src="../google-code-prettify/jquery-1.10.2.js"></script>
<script>
var map;
//宣告圖示並指定樣式
var shopIcon_all = new CIcon();
shopIcon_all.image = "../image/blue_all.png";
shopIcon_all.iconSize = new CSize(20, 20);
shopIcon_all.iconAnchor = new CPoint(0, 0);
shopIcon_all.infoWindowAnchor = new CPoint(0, 0);
var shopIcon_point = new CIcon();
shopIcon_point.image = "../image/blue_point.png";
shopIcon_point.iconSize = new CSize(20, 20);
shopIcon_point.iconAnchor = new CPoint(0, 0);
shopIcon_point.infoWindowAnchor = new CPoint(0, 0);
//宣告繪製多邊形樣式
var polyStyle = {weight:5, opacity:0.6, fopacity:0.3, simplify:false, addArrow:false};

//宣告所需物件
var shopCollection;
var Shops = [], currShops_ll=[], currShops_cluster;
var LLCollection_shop=[], LLCollection_shop_mkr=[], LLCollection_shop_rect=[],textobj={};

function showShopCollection()
{
	shopCollection.show("canvas");
}

function drawToMap() {
    map.clearOverlays();
    var presenttype = "point", _cllb_obj = {}, _cllb_obj_ll=[];
    presenttype = document.getElementById("selectType").value;
//     if(presenttype=="total") 
//     {
//         //直接呈現全部
//         //先清空內容，避免重複資料
//         shopCollection.clear();
//         LLCollection_shop_mkr=[];
//         //逐一宣告成 CMarker 物件，加入 COverlayCollection 物件中
//         for(var _i = 0, _sizei=Shops.length; _i<_sizei; _i++) {
//             LLCollection_shop_mkr.push(new CMarker(Shops[_i].ll, {icon:shopIcon_point, clickable: true, draggable: false}));
//             shopCollection.push(LLCollection_shop_mkr[(LLCollection_shop_mkr.length - 1)]);
//         }
//         //以 show("canvas") 的方式疊加，表示是以繪製方式，若不加入 "canvas" 參數，則會直接以 CMarker 物件疊加，效能較低。
//         //setTimeout(function(){ shopCollection.show("canvas"); }, 100);
// 		setTimeout("showShopCollection()", 100);
//     }
    else {
        currShops_ll=[];
        for(var _i = 0, _sizei=Shops.length; _i<_sizei; _i++) {
            currShops_ll.push(Shops[_i].ll);
        }
        if(currShops_ll.length > 0) {
            //與聚集相關之呈現方式，均須透過宣告 CMarkerCluster 達成
            currShops_cluster = new CMarkerCluster(currShops_ll, map,{icon:shopIcon_all});
            //初始化陣列
            map.removeMultiOverlay(LLCollection_shop_mkr);
            LLCollection_shop_mkr=[];
            LLCollection_shop_rect=[];
            LLCollection_shop=[];
            
            //透過 CMarkerCluster 物件的 ClusterByGrid 取得以網格方式切割後的的聚集結果
            LLCollection_shop = currShops_cluster.ClusterByGrid();
            if(presenttype=="grid") 
            {
                //格網方式呈現
                for(var i = 0; i < LLCollection_shop.length; i++)
                {
                    numbersin = LLCollection_shop[i].membersize;
                    //透過 CMarkerCluster 物件的 getGridBoundByClusteredIndex 取得切割的網格及內含數量
                    //網格原本是 CLatLngBounds 物件，自行取得左下、右上座標，重組成座標陣列
                    _cllb_obj = currShops_cluster.getGridBoundByClusteredIndex(LLCollection_shop[i].index);
                    _cllb_obj_ll = [];
                    _cllb_obj_ll.push(_cllb_obj.getSouthWest());
                    _cllb_obj_ll.splice(1, 0, new CLatLng(_cllb_obj.getSouthWest().lat(), _cllb_obj.getNorthEast().lng()));
                    _cllb_obj_ll.push(_cllb_obj.getNorthEast());
                    _cllb_obj_ll.push( new CLatLng(_cllb_obj.getNorthEast().lat(), _cllb_obj.getSouthWest().lng()));
                    if(_cllb_obj instanceof CLatLngBounds) 
                    {
                        //將重組的座標陣列宣告為 CPolygon 物件，即可繪製出矩形，再以內含數量決定填入顏色透明度
                        LLCollection_shop_rect.push(new CPolygon(_cllb_obj_ll,polyStyle));
                        LLCollection_shop_rect[i].setStyle({color:"#0080C0", fcolor:"#0080C0"});
                        if(numbersin > 99)
                            LLCollection_shop_rect[i].setStyle({fopacity:1.0});
                        else if(numbersin > 50)
                            LLCollection_shop_rect[i].setStyle({fopacity:0.6});
                        else if(numbersin > 10)
                            LLCollection_shop_rect[i].setStyle({fopacity:0.4});
                        else
                            LLCollection_shop_rect[i].setStyle({fopacity:0.2});
                    }
                    map.addOverlay(LLCollection_shop_rect[i]);
                }
                
            }
            else 
            {
                var outputgrid=[], gridobj={};
                for(var i = 0; i < LLCollection_shop.length; i++)
                {
                    gridobj={};
                    lltemp = LLCollection_shop[i].ll;
                    numbersin = LLCollection_shop[i].membersize;
                    
                    if(presenttype=="point")
                    {
                        //透過 CMarkerCluster 物件，直接取得以網格方式切割後的的聚集結果
                        //以 CMarker 方式將聚集後的結果疊加至地圖上
                        textobj={text:numbersin.toString(), textleft:5, texttop:5 };
                        
                        LLCollection_shop_mkr.push(new CMarker(lltemp,{icon: shopIcon_all, clickable: false, draggable: false}))
                        LLCollection_shop_mkr[i].setIcon(shopIcon_all);	
                        
                        if(numbersin > 99)
                        {
                            shopIcon_all.iconSize = new CSize(50, 50);
                            shopIcon_all.iconAnchor = new CPoint(25, 25);
                            textobj.textleft=12;
                            textobj.texttop=13;
                        }
                        else if(numbersin > 50)
                        {
                            shopIcon_all.iconSize = new CSize(40, 40);
                            shopIcon_all.iconAnchor = new CPoint(20, 20);
                            textobj.textleft=10;
                            textobj.texttop=10;
                        }
                        else if(numbersin > 9)
                        {
                            shopIcon_all.iconSize = new CSize(30, 30);
                            shopIcon_all.iconAnchor = new CPoint(15, 15);
                            textobj.textleft=6;
                            textobj.texttop=5;
                        }
                        else 
                        {
                            shopIcon_all.iconSize = new CSize(20, 20);
                            shopIcon_all.iconAnchor = new CPoint(10, 10);
                            textobj.textleft=5;
                            textobj.texttop=0;
                        }
                        LLCollection_shop_mkr[i].setIcon(shopIcon_all);
                        map.addOverlay(LLCollection_shop_mkr[i]);
                        if(numbersin > 1)
                            LLCollection_shop_mkr[i].setText(textobj);
                    }
                    else
                    {
                        gridobj.ll=lltemp;
                        gridobj.percentage=numbersin;
                        outputgrid.push(gridobj);
                    }
                }
                //藉由前面 for loop 中指定 outputgrid 內容，直接呼叫 CDensityView.showDensity 函式繪製密度圖
                if(presenttype=="density")
                    CDensityView.showDensity(map, outputgrid);
            }
        }
    }
}

//載入資料
function initData() {
    $.ajax({
        type: "GET",
        url: "shop.txt",
        dataType: "text",
        success: function(data) {processData(data);}
    });
}
//進行資料格式安排
function processData(_allText) {
    var allTextLines = _allText.split(/\r\n|\n/);
    for (var i=0; i<allTextLines.length; i++) {
        if(allTextLines[i]!="") {
            var shopObj = {};
            var data = allTextLines[i].split(',');
            for(var _j = 0, _sizej = data.length; _j<_sizej; _j++) {
                data[_j] = data[_j].trim();
            }
            shopObj.ll = new CLatLng(data[1], data[0]);
            Shops.push(shopObj);
        }
    }
    //實際繪製
    drawToMap();
}

function loadMap() {
    map = new CMap(document.getElementById("map"));
    var ll = new CLatLng(25.0476, 121.5170);
    map.setCenter(ll, 4);
    map.addControl(C_SCALE_CTRL);
    map.addControl(C_LARGE_ZOOM_V);
    map.enableScrollWheelZoom(true);
    if(map.getMaxZoom()>13)
        map.setZoom(12);
	shopCollection = new COverlayCollection(map);
    
    //每次點擊、縮放，都進行重繪
    CEvent.addListener(map, "click", function () {drawToMap();});
    CEvent.addListener(map, "zoomend", function () {drawToMap();});
    
}

</script>
</head>
<body onload="loadMap();initData()" onunload="CUnload()">
點擊滑鼠左鍵決定規劃路徑點，滑鼠右鍵出現選單。
<div id="map" style="position:absolute; left:0px; top:0px; width:600px; height:500px; border:1px solid #000;"></div>

<div style="position:absolute; left:0px; top:505px;">呈現型態：<select id="selectType" onchange="drawToMap()"><option value="point">點聚集</option><option value="grid">格網</option><option value="density">密度圖</option><option value="total">全部</option></select></div>
</body>
</html>