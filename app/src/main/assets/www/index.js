function $(id)
{
    return document.getElementById(id);
}

// 設定網頁載入完成傾聽器
document.addEventListener("DOMContentLoaded", init);

var map;

// init方法
function init()
{
      // 使用Google Map API定義經緯度物件
      var myLatLng = new google.maps.LatLng(24.947292, 121.229086);  // 緯度, 經度
      
      // 定義地圖參數物件：https://developers.google.com/maps/documentation/javascript/controls?hl=zh-tw
      var myOption = 
      {
            zoom: 17,   // 地圖的遠近
            center: myLatLng, // 地圖的中心點
            mapTypeId: 'roadmap',  // 地圖樣式：https://developers.google.com/maps/documentation/javascript/maptypes?hl=zh-tw
            zoomControl: false
      }

      // 關聯HTML裡的id='mapCanvas'元素     
      var mapCanavs = $('mapCanvas');

      // 建立地圖物件
      map = new google.maps.Map(mapCanavs, myOption);

      // 建立Marker
      addMarker(24.947292, 121.229086);
      addMarker2(24.953850, 121.225793);

      moveTo(24.953850, 121.225793);
}

// 改變地圖的中心位置
function moveTo(latitude, longitude)
{
    // 使用Google Map API定義經緯度物件
    var myLatLng = new google.maps.LatLng(latitude, longitude);  // 緯度, 經度

    map.panTo(myLatLng);
    addMarker(latitude, longitude);
}

var locationMarker;

// 建立Marker
function addMarker(latitude, longitude)
{
     // 使用Google Map API定義經緯度物件
      var myLatLng = new google.maps.LatLng(latitude, longitude);  // 緯度, 經度

     // 如果Marker已經存在，必須先移除並銷毀
     if(locationMarker)
      {
          locationMarker.setMap(null);   // 從地圖上移除Marker
          locationMarker = null;                // 銷毀物件
      }

      // 建立新的Marker
      locationMarker = new google.maps.Marker(
      {
              position: myLatLng,   // Marker要顯示的在地圖的哪裡
              map: map,
              icon: "car.png"
      });   

      // 設定Marker點擊傾聽器
      google.maps.event.addListener(locationMarker, "click", onMarkerClicked);
}

// Marker事件方法
function  onMarkerClicked()
{
    var infoWindow = new google.maps.InfoWindow(
    { 
          // InfoWindow要顯示的內容，格式為HTML
          content:  "<a target='_blank' href='http://www.aaronlife.com'><img src='pic.jpg' width='220px' height='150px' /></a><p>我在這裡!</p>"
    });

     // 將InfoWindow顯示出來
     // 參數1: InfoWindow要出現的地圖
     // 參數2: InfoWindow要出現的Marker位置
     infoWindow.open(map, locationMarker);

     if(aaronho) // 判斷物件有無存在
     {
        aaronho.showToast('Hello GPS');
     }
}


var locationMarker2;

// 建立Marker
function addMarker2(latitude, longitude)
{
     // 使用Google Map API定義經緯度物件
      var myLatLng = new google.maps.LatLng(latitude, longitude);  // 緯度, 經度

     // 如果Marker已經存在，必須先移除並銷毀
     if(locationMarker2)
      {
          locationMarker2.setMap(null);   // 從地圖上移除Marker
          locationMarker2 = null;                // 銷毀物件
      }

      // 建立新的Marker
      locationMarker2 = new google.maps.Marker(
      {
              position: myLatLng,   // Marker要顯示的在地圖的哪裡
              map: map,
              icon: "car.png"
      });   

      // 設定Marker點擊傾聽器
      google.maps.event.addListener(locationMarker2, "click", onMarkerClicked2);
}

// Marker事件方法
function  onMarkerClicked2()
{
    var infoWindow = new google.maps.InfoWindow(
    { 
          // InfoWindow要顯示的內容，格式為HTML
          content:  "<a target='_blank' href='http://www.aaronlife.com'><img src='pic.jpg' width='220px' height='150px' /></a><p>我在這裡!</p>"
    });

     // 將InfoWindow顯示出來
     // 參數1: InfoWindow要出現的地圖
     // 參數2: InfoWindow要出現的Marker位置
     infoWindow.open(map, locationMarker2);
}




