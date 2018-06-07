package com.helloword.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.helloword.bin.InfoBean;
import com.helloword.interfaces.HttpCallBack;
import com.helloword.util.HttpRequestImpl;

import okhttp3.Call;

public class AddInfomationToServer {
	
	private static String filePath="C:\\Users\\MrRight\\Desktop\\龙岗区市政排水管涵地面坍塌隐患检测评估情况一览表（第二批）(小赖).xlsx";
	//与排水管涵无关联的132处隐患点 //485处隐患
    private static final String sheetName="485处隐患";
    private static Workbook workBook;
    private static Sheet sheet;
    private static ExecutorService executorService=Executors.newFixedThreadPool(2);
    private static HttpRequestImpl https=new HttpRequestImpl();
    //龙岗区第二批市政排水管涵安全隐患排查  %E9%BE%99%E
    private  static String url="http://lgsfb.51vip.biz:12192/ywbase/danger/saveDanger.action?evaluation_project_name=";

	public static void main(String[] args) {
		String encoderStr = null;
		try {
			encoderStr = URLEncoder.encode("龙岗区第二批市政排水管涵安全隐患排查","UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url=url+encoderStr;
		System.out.println(url);
		load();
		getSheetDataWithMap();
		
	}

	
	private static void load() {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(filePath));
            workBook = WorkbookFactory.create(inStream);
            sheet = workBook.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	
	
	private static void getSheetDataWithMap() {
		int numOfRows = sheet.getLastRowNum() + 1;
		Map<String, String> map;
        for (int i = 4; i < numOfRows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
            	map=new HashMap<>();
            	map.put("project_sum", "3417");
            	map.put("examinine_date", "2015-2016");
            	map.put("accomplish_information", "已完成");
            	
            	String gridId=getGridId("");
            	map.put("gridCode", "1524");
            	map.put("administrative", "1524");//和gridCode是同一个ID
            	
            	map.put("operateadd", "add");
            	String xyl="";
            	String xuhao="";
            	String values;
            	Cell cell;
                for (int j = 5; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    cell.setCellType(CellType.STRING);
                    values=cell.getStringCellValue();
                    
                    if(j==5) {
                    	xuhao=cell.getStringCellValue();
                    	if(xuhao==null||xuhao.trim().equals("")) {break;}
                    }else if(j==6) {
                    	map.put("information_type", values==null?"":values);//地下空洞隐患
                    }else if(j==7) {
                    	map.put("district", values==null?"":values);
                    }else if(j==8) {
                    	map.put("street", values==null?"":values);
                    }else if(j==9) {
                    	map.put("road", values==null?"":values);
                    }else if(j==10) {
                    	map.put("location", values==null?"":values);//Y0423→Y0424
                    }else if(j==11) {
                    	xyl="("+cell.getStringCellValue()+",";
                    }else if(j==12) {
                    	xyl=xyl+cell.getStringCellValue()+")";
                    	map.put("xy", xyl==null?"":xyl);
                    }else if(j==13) {
                    	map.put("length", values==null?"":values);
                    }else if(j==14) {
                    	map.put("width", values==null?"":values);
                    }else if(j==15) {
                    	map.put("deepness", values==null?"":values);
                    }else if(j==16) {
                    	map.put("drain_pipe_type", values==null?"":values);
                    }else if(j==17) {
                    	map.put("pipe_material", values==null?"":values);
                    }else if(j==18) {
                    	//
                    }else if(j==19) {
                    	map.put("pipedeepness", values==null?"":values);
                    }else if(j==20) {
                    	map.put("repair_level", values==null?"":values);
                    }else if(j==21) {
                    	map.put("maintenance_level", values==null?"":values);
                    }else if(j==22) {
                    	map.put("potential_economic_losses", values==null?"":values);
                    }else if(j==23) {
                    	map.put("building_potential_threats", values==null?"":values);
                    }else if(j==24) {
                    	map.put("potential_threat_people", values==null?"":values);
                    }else if(j==25) {
                    	map.put("hidden_danger_level", values==null?"":values);//III
                    }else if(j==26) {
                    	map.put("del_with_measure", values==null?"":values);
                    }
                }
                map.put("project_information_id", "");
                map.put("evaluation_project_name","龙岗区第二批市政排水管涵安全隐患排查");//检测评估项目名称
                map.put("cavity_type", "");//地下空洞（空洞类型）
                map.put("pipe_length",""); //管道长
                //addtoserver(map,i);
                try {
					Thread.sleep(4300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                System.out.println(map.toString());
            }
        }
        System.out.println("***OVER***");
        getGridId("");
	}
	

	/**
	 * 获取ID  注意  有5个重复的区划  ，而且依据什么来判断行政区划
	 */
	private static String getGridId(String keyworld) {
		String gridId="";
		if(keyworld.contains("振业")) {
			gridId="1684";//
		}else if(keyworld.contains("怡锦")) {
			gridId="1683";  //怡锦    1683
		}else if(keyworld.contains("华乐")) {
			gridId="1682";  //华乐	1682
		}else if(keyworld.contains("志盛")) {
			gridId="1681";  //志盛	1681
		}else if(keyworld.contains("华侨新村")) {
			gridId="1680";  //华侨新村	1680
		}else if(keyworld.contains("荷坳")) {
			gridId="1679";  //荷坳	1679
		}else if(keyworld.contains("银荷")) {
			gridId="1678";  //银荷	1678
		}else if(keyworld.contains("横岗")) {		//************
			gridId="1677";  //横岗	1677
		}else if(keyworld.contains("横岗")) {
			gridId="1669";  //横岗	1669
		}else if(keyworld.contains("大康")) {
			gridId="1676";  //大康	1676
		}else if(keyworld.contains("六约")) {
			gridId="1675";  //六约	1675
		}else if(keyworld.contains("安良")) {
			gridId="1674";  //安良	1674
		}else if(keyworld.contains("宝龙")) {
			gridId="1668";  //宝龙	1668
		}else if(keyworld.contains("同乐")) {
			gridId="1667";  //同乐	1667
		}else if(keyworld.contains("南约")) {
			gridId="1666";  //南约	1666
		}else if(keyworld.contains("南三")) {
			gridId="1640";	//南三	1640
		}else if(keyworld.contains("大芬")) {
			gridId="1641";	//大芬	1641
		}else if(keyworld.contains("水径")) {
			gridId="1642";	//水径	1642
		}else if(keyworld.contains("翠湖")) {
			gridId="1643";	//翠湖	1643
		}else if(keyworld.contains("东方半岛")) {
			gridId="1644";	//东方半岛   1644
		}else if(keyworld.contains("文景")) {
			gridId="1645";	//文景	1645
		}else if(keyworld.contains("木棉湾")) {
			gridId="1646";	//木棉湾	1646
		}else if(keyworld.contains("可园")) {
			gridId="1647";	//可园	1647
		}else if(keyworld.contains("龙城")) {
			gridId="1648";	//龙城	1648
		}else if(keyworld.contains("爱联")) {
			gridId="1649";	//爱联	1649
		}else if(keyworld.contains("五联")) {
			gridId="1651";	//五联	1651
		}else if(keyworld.contains("龙西")) {
			gridId="1650";  //龙西	1650
		}else if(keyworld.contains("回龙埔")) {
			gridId="1652";  //回龙埔	1652
		}else if(keyworld.contains("紫薇")) {
			gridId="1653";  //紫薇	1653
		}else if(keyworld.contains("尚景")) {
			gridId="1654";  //尚景	1654
		}else if(keyworld.contains("盛平")) {
			gridId="1656";  //盛平 	1656
		}else if(keyworld.contains("愉园")) {
			gridId="1655";  //愉园	1655
		}else if(keyworld.contains("黄阁坑")) {
			gridId="1657";  //黄阁坑	1657
		}else if(keyworld.contains("龙红格")) {
			gridId="1658";  //龙红格	1658
		}else if(keyworld.contains("新生")) {
			gridId="1660";  //新生	1660
		}else if(keyworld.contains("龙岗")) {		//************
			gridId="1661";  //龙岗	1661
		}else if(keyworld.contains("龙岗")) {
			gridId="1659";  //龙岗	1659
		}else if(keyworld.contains("龙东")) {
			gridId="1662";  //龙东	1662
		}else if(keyworld.contains("南联")) {
			gridId="1663";  //南联	1663
		}else if(keyworld.contains("龙岗墟")) {
			gridId="1664";  //龙岗墟  1664
		}else if(keyworld.contains("平南")) {
			gridId="1665";  //平南	1665
		}else if(keyworld.contains("松柏")) {
			gridId="1670";  //松柏    1670
		}else if(keyworld.contains("保安")) {
			gridId="1671";  //保安	1671
		}else if(keyworld.contains("四联")) {
			gridId="1672";  //四联	1672
		}else if(keyworld.contains("西坑")) {
			gridId="1673";  //西坑	1673
		}else if(keyworld.contains("樟树布")) {
			gridId="1609";	//樟树布	1609
		}else if(keyworld.contains("厦村")) {
			gridId="1610";	//厦村	1610
		}else if(keyworld.contains("宝岭")) {
			gridId="1611";	//宝岭	1611
		}else if(keyworld.contains("康乐")) {
			gridId="1612";	//康乐	1612
		}else if(keyworld.contains("坂田")) {		//************
			gridId="1613";	//坂田	1613
		}else if(keyworld.contains("坂田")) {
			gridId="1617";	//坂田	1617
		}else if(keyworld.contains("岗头")) {
			gridId="1614";	//岗头	1614
		}else if(keyworld.contains("雪象")) {
			gridId="1615";	//雪象	1615
		}else if(keyworld.contains("四季花城")) {
			gridId="1616";	//四季花城	1616
		}else if(keyworld.contains("杨美")) {
			gridId="1618";	//杨美	1618
		}else if(keyworld.contains("马安堂")) {
			gridId="1619";	//马安堂	1619
		}else if(keyworld.contains("五和")) {
			gridId="1620";	//五和	1620
		}else if(keyworld.contains("南坑")) {
			gridId="1621";	//南坑	1621
		}else if(keyworld.contains("大发埔")) {
			gridId="1622";	//大发埔	1622
		}else if(keyworld.contains("布吉")) {		//************
			gridId="1623";	//布吉	1623
		}else if(keyworld.contains("布吉")) {
			gridId="1639";	//布吉	1639
		}else if(keyworld.contains("布吉墟")) {
			gridId="1624";	//布吉墟	1624
		}else if(keyworld.contains("龙岭")) {
			gridId="1625";	//龙岭	1625
		}else if(keyworld.contains("德兴")) {
			gridId="1627";	//德兴	1627
		}else if(keyworld.contains("龙珠")) {
			gridId="1628";	//龙珠	1628
		}else if(keyworld.contains("丽湖")) {
			gridId="1629";	//丽湖	1629
		}else if(keyworld.contains("国展")) {
			gridId="1630";	//国展	1630
		}else if(keyworld.contains("茂业")) {
			gridId="1631";	//茂业	1631
		}else if(keyworld.contains("中海怡翠")) {
			gridId="1632";	//中海怡翠	1632
		}else if(keyworld.contains("甘坑")) {
			gridId="1633";	//甘坑	1633
		}else if(keyworld.contains("三联")) {
			gridId="1634";	//三联	1634
		}else if(keyworld.contains("凤凰")) {
			gridId="1635";	//凤凰	1635
		}else if(keyworld.contains("罗岗")) {
			gridId="1636";	//罗岗	1636
		}else if(keyworld.contains("金排")) {
			gridId="1637";	//金排	1637
		}else if(keyworld.contains("光华")) {
			gridId="1638";	//光华	1638
		}else if(keyworld.contains("沙塘布")) {
			gridId="1608";	//沙塘布	1608
		}else if(keyworld.contains("吉厦")) {
			gridId="1607";	//吉厦	1607
		}else if(keyworld.contains("南岭村")) {
			gridId="1606";	//南岭村	1606
		}else if(keyworld.contains("丹竹头")) {
			gridId="1605";	//丹竹头	1605
		}else if(keyworld.contains("下李朗")) {
			gridId="1604";	//下李朗	1604
		}else if(keyworld.contains("上李朗")) {
			gridId="1603";	//上李朗	1603
		}else if(keyworld.contains("南龙")) {
			gridId="1602";	//南龙	1602
		}else if(keyworld.contains("沙湾")) {
			gridId="1601";	//沙湾	1601
		}else if(keyworld.contains("南湾")) {
			gridId="1600";	//南湾	1600
		}else if(keyworld.contains("四方埔")) {
			gridId="1546";	//四方埔	1546
		}else if(keyworld.contains("年丰")) {
			gridId="1545";	//年丰	1545
		}else if(keyworld.contains("六联")) {
			gridId="1544";	//六联	1544
		}else if(keyworld.contains("中心")) {
			gridId="1543";	//中心	1543
		}else if(keyworld.contains("坪东")) {
			gridId="1542";	//坪东	1542
		}else if(keyworld.contains("坪西")) {
			gridId="1541";	//坪西	1541
		}else if(keyworld.contains("怡心")) {
			gridId="1540";	//怡心	1540
		}else if(keyworld.contains("坪地")) {
			gridId="1539";	//坪地	1539
		}else if(keyworld.contains("力昌")) {
			gridId="1537";	//力昌	1537
		}else if(keyworld.contains("禾花")) {
			gridId="1536";	//禾花	1536
		}else if(keyworld.contains("新南")) {
			gridId="1534";	//新南	1534
		}else if(keyworld.contains("上木古")) {
			gridId="1533";	//上木古	1533
		}else if(keyworld.contains("辅城坳")) {
			gridId="1532";	//辅城坳	1532
		}else if(keyworld.contains("白泥坑")) {
			gridId="1531";	//白泥坑	1531
		}else if(keyworld.contains("平湖")) {         //************
			gridId="1530";	//平湖	1530
		}else if(keyworld.contains("平湖")) {
			gridId="1525";	//平湖	1525
		}else if(keyworld.contains("鹅公岭")) {
			gridId="1529";	//鹅公岭	1529
		}else if(keyworld.contains("山厦")) {
			gridId="1528";	//山厦	1528
		}else if(keyworld.contains("新木")) {
			gridId="1527";	//新木	1527
		}else if(keyworld.contains("良安田")) {
			gridId="1526";	//良安田	1526
		}else {
			gridId="1524";	//龙岗区	1524
		}
		return gridId;
	}
	
	
	private static void addtoserver(Map<String, String> params,final int count) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				https.multiParamHttpPost(params, url, new HttpCallBack() {
					@Override
					public void onResponse(String s, int resultCode) {
						System.out.println(count+"**http success****"+s);
					}
					
					@Override
					public void onError(Call call, Exception e, int errorCode) {
						System.out.println("http error");
					}
				});
			}
		});
	}
	
	
	
	
	
	
	private static void getSheetDataWithBean() {
        int numOfRows = sheet.getLastRowNum() + 1;
        for (int i = 4; i < numOfRows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
            	InfoBean infoBean=new InfoBean();
            	infoBean.setProject_sum("3417");
            	infoBean.setExaminine_date("2015-2016");
            	infoBean.setAccomplish_information("已完成");
            	infoBean.setGridCode("1524");
            	infoBean.setOperateadd("add");
            	String xyl="";
            	String xuhao="";
            	Cell cell;
                for (int j = 5; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    cell.setCellType(CellType.STRING);
                    if(j==5) {
                    	xuhao=cell.getStringCellValue();
                    	if(xuhao==null||xuhao.trim().equals("")) {
                    		break;
                    	}
                    }else if(j==6) {
                    	infoBean.setInformation_type(cell.getStringCellValue());
                    }else if(j==7) {
                    	infoBean.setDistrict(cell.getStringCellValue());
                    }else if(j==8) {
                    	infoBean.setStreet(cell.getStringCellValue());
                    }else if(j==9) {
                    	infoBean.setRoad(cell.getStringCellValue());
                    }else if(j==10) {
                    	infoBean.setLocation(cell.getStringCellValue());
                    }else if(j==11) {
                    	xyl="("+cell.getStringCellValue()+",";
                    }else if(j==12) {
                    	xyl=xyl+cell.getStringCellValue()+")";
                    	infoBean.setXy(xyl);
                    }else if(j==13) {
                    	infoBean.setLength(cell.getStringCellValue());
                    }else if(j==14) {
                    	infoBean.setWidth(cell.getStringCellValue());
                    }else if(j==15) {
                    	infoBean.setDeepness(cell.getStringCellValue());
                    }else if(j==16) {
                    	infoBean.setDrain_pipe_type(cell.getStringCellValue());
                    }else if(j==17) {
                    	infoBean.setPipe_material(cell.getStringCellValue());
                    }else if(j==18) {
                    	//
                    }else if(j==19) {
                    	infoBean.setPipedeepness(cell.getStringCellValue());
                    }else if(j==20) {
                    	infoBean.setRepair_level(cell.getStringCellValue());
                    }else if(j==21) {
                    	infoBean.setMaintenance_level(cell.getStringCellValue());
                    }else if(j==22) {
                    	infoBean.setPotential_economic_losses(cell.getStringCellValue());
                    }else if(j==23) {
                    	infoBean.setBuilding_potential_threats(cell.getStringCellValue());
                    }else if(j==24) {
                    	infoBean.setPotential_threat_people(cell.getStringCellValue());
                    }else if(j==25) {
                    	infoBean.setHidden_danger_level(cell.getStringCellValue());
                    }else if(j==26) {
                    	infoBean.setDel_with_measure(cell.getStringCellValue());
                    }
                }
                System.out.println(xuhao+"--"+i+"--"+infoBean.toString());
                //addtoserver(map,i);
            }
        }
    }
	
}
