vietpdb123

package com.bkav.aic.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tempuri.SMSServiceStub;
import org.tempuri.SMSServiceStub.SendWithPriority;
import org.tempuri.SMSServiceStub.SendWithPriorityResponse;

import com.bkav.aic.bmm.object.MissionReport;
import com.bkav.aic.bmm.object.tbl_User;
import com.bkav.aic.coresql.BIChartMixData;
import com.bkav.aic.coresql.BIChartPureData;
import com.bkav.aic.coresql.BICondition;
import com.bkav.aic.dbconnection.MySqlConnection;
import com.bkav.aic.dbconnection.SQLServerConnection;
import com.bkav.aic.define.ErrorCode;
import com.bkav.aic.define.RetValData;
import com.bkav.aic.object.AdvancedFormula;
import com.bkav.aic.object.BIColumn;
import com.bkav.aic.object.BIFormula;
import com.bkav.aic.object.BITable;
import com.bkav.aic.object.BIView;
import com.bkav.aic.object.BIViewGroup;
import com.bkav.aic.object.ChartObject;
import com.bkav.aic.object.ChartPresentObject;
import com.bkav.aic.object.ChartPresentObjectType;
import com.bkav.aic.object.DashboardConfig;
import com.bkav.aic.object.Dashboard_Resource;
import com.bkav.aic.object.Document;
import com.bkav.aic.object.Field;
import com.bkav.aic.object.Formula;
import com.bkav.aic.object.FormulaChart;
import com.bkav.aic.object.FormulaChartDetail;
import com.bkav.aic.object.Group;
import com.bkav.aic.object.Statistical;
import com.bkav.aic.object.Statistical_Dashboard;
import com.bkav.aic.object.Statistical_Widget;
import com.bkav.aic.object.User;
import com.bkav.aic.object.Widget_Resource;
import com.bkav.aic.service.BIMICService;
import com.bkav.aic.utils.Base64Utils;
import com.bkav.aic.utils.Utility;

public class Controller {
	private static final String configPath = "C://config//";

	@SuppressWarnings("unused")
	private final int _____formula_____ = 0;

	public RetValData createFormula(String jsonParam) {
		// check input
		try {
			int type = 0;
			String name = null;
			JSONObject view = null;
			JSONObject data = null;

			JSONObject json = new JSONObject(jsonParam1);
			vietpdb123
			if (Utility.isJsonKeyExistedAndNotNull(json, "type")) {
				type = json.getInt("type");
			} else {
				return new RetValData(ErrorCode.Invalid_Input.code, "",
						"json Param invalid: type null");
			}

			if (Utility.isJsonKeyExistedAndNotNull(json, "view")) {
				view = json.getJSONObject("view");
			} else {
				return new RetValData(ErrorCode.Invalid_Input.code, "",
						"json Param invalid: view null");
			}

			if (Utility.isJsonKeyExistedAndNotNull(json, "data")) {
				data = json.getJSONObject("data");
				if (Utility.isJsonKeyExistedAndNotNull(data, "alias")) {
					name = data.getString("alias");
				} else {
					return new RetValData(ErrorCode.Invalid_Input.code, "",
							"json Param invalid: allias null");
				}
			} else {
				return new RetValData(ErrorCode.Invalid_Input.code, "",
						"json Param invalid: data null");
			}

			RetValData checkRet = checkFormulaValidated(view, data);
			if (type == 2 || checkRet.getCode() != ErrorCode.OK.code) {
				if (type == 2) {
					checkRet.setData("Công thức hợp lệ");
				}
				return checkRet;
			}

			BIFormula formula = new BIFormula();
			formula.setJsonValue(data.toString());
			formula.setName(name);
			formula.setView(view.toString());
			formula.setSQL("sql");
			System.out.println(data.toString());
			System.out.println(view.toString());

			MySqlConnection con = new MySqlConnection();
			int ret = con.insert(formula);
			if (ret != ErrorCode.OK.code) {
				return new RetValData(ret, "Tạo công thức thất bại!",
						"insert biformula fail");
			}
			return new RetValData("Tạo công thức thành công!");
		} catch (JSONException e) {
			e.printStackTrace();
			return new RetValData(ErrorCode.Invalid_Input.code, e.getMessage(),
					"json Param invalid");
		}
	}

	public String getFormulaChartDetail(int chartId) {
		MySqlConnection conn = new MySqlConnection();
		List<FormulaChartDetail> list = conn.getChartDetailById(chartId);
		if (list.size() > 0) {
			JSONArray arrayJsonArray = new JSONArray();
			for (FormulaChartDetail chart : list) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("type", chart.getType());
					obj.put("chartname", chart.getChartname());
					obj.put("option", chart.getChartoption());
					obj.put("xaxis", chart.getXaxis());
					obj.put("dataChart", conn.getColumnResult(chart.getSql()));
					arrayJsonArray.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return arrayJsonArray.toString();
		}
		return "";
	}

	public String getFormulaChartDetailTemp(int chartId, String advanceQuery) {
		MySqlConnection conn = new MySqlConnection();
		List<FormulaChartDetail> list = conn.getChartDetailById(chartId);
		if (list.size() > 0) {
			JSONArray arrayJsonArray = new JSONArray();
			for (FormulaChartDetail chart : list) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("type", chart.getType());
					obj.put("chartname", chart.getChartname());
					obj.put("option", chart.getChartoption());
					obj.put("xaxis", chart.getXaxis());
					obj.put("dataChart",
							conn.getColumnResult(chart.getSql() + " "
									+ advanceQuery));
					arrayJsonArray.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return arrayJsonArray.toString();
		}
		return "";
	}

	public String getFormulaChartbySttc(int sttcId) {
		MySqlConnection conn = new MySqlConnection();
		List<FormulaChart> list = conn.getChartBySttc(sttcId);
		if (list.size() > 0) {
			JSONArray arrayJsonArray = new JSONArray();
			for (FormulaChart chart : list) {
				JSONObject obj = new JSONObject();
				try {
					Statistical tk = conn.get(Statistical.class, sttcId);
					obj.put("sttcId", tk.getId());
					obj.put("sttcName", tk.getName());
					obj.put("idchart", chart.getId());
					obj.put("chartname", chart.getChartname());
					obj.put("type", chart.getType());
					obj.put("option", chart.getChartoption());
					obj.put("xaxis", chart.getXaxis());
					obj.put("dataChart", conn.getColumnResult(chart.getSql()));
					obj.put("statusId", chart.getStatusId());
					if (chart.getStatusId() == 0)
						obj.put("statusName", "Ẩn");
					else {
						obj.put("statusName", "Hiển thị");
					}
					arrayJsonArray.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return arrayJsonArray.toString();
		}
		return "";
	}

	public String getFormulaChartbySttcRender(int sttcId) {
		MySqlConnection conn = new MySqlConnection();
		List<FormulaChart> list = conn.getChartBySttc(sttcId);
		if (list.size() > 0) {
			JSONArray arrayJsonArray = new JSONArray();
			for (FormulaChart chart : list) {
				JSONObject obj = new JSONObject();
				try {
					if (chart.getStatusId() == 0)
						continue;
					Statistical tk = conn.get(Statistical.class, sttcId);
					obj.put("sttcId", tk.getId());
					obj.put("sttcName", tk.getName());
					obj.put("idchart", chart.getId());
					obj.put("chartname", chart.getChartname());
					obj.put("type", chart.getType());
					obj.put("option", chart.getChartoption());
					obj.put("xaxis", chart.getXaxis());
					obj.put("dataChart", conn.getColumnResult(chart.getSql()));
					obj.put("statusId", chart.getStatusId());

					arrayJsonArray.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return arrayJsonArray.toString();
		}
		return "";
	}

	/*
	 * public String renderChartOfFormula(String chartName, int type, String
	 * options, String xaxis, String sql) { MySqlConnection conn = new
	 * MySqlConnection(); JSONObject obj = new JSONObject(); JSONArray arr = new
	 * JSONArray(); try { obj.put("idchart", 1); obj.put("chartname",
	 * chartName); obj.put("type", type); obj.put("option", options);
	 * obj.put("xaxis", xaxis); obj.put("dataChart", conn.getColumnResult(sql));
	 * arr.put(obj); } catch (JSONException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * return arr.toString(); }
	 */
	public String renderChartOfFormula(String chartName, String xaxis,
			String sql) {
		MySqlConnection con = new MySqlConnection();
		boolean iChecxAxis = false;
		try {
			JSONArray array = new JSONArray(con.getColumnResult(sql));
			System.out.println("array :" + array);
			List<String> columnTemp = keyJSONOBJ(array.getJSONObject(0));
			List<String> columns = new ArrayList<String>();
			JSONObject objDataTable = new JSONObject();
			JSONArray arrayCols = new JSONArray();
			JSONArray arrayRows = new JSONArray();

			columns.add(xaxis);
			for (String col : columnTemp) {
				if (!col.equals(xaxis))
					columns.add(col);
				else
					iChecxAxis = true;
			}
			if (!iChecxAxis) {
				System.out.println("Error mapping iChecxAxis");
				return "";
			}
			for (String name : columns) {
				JSONObject objCol = new JSONObject();
				System.out.println("column: " + name);
				if (name.equals(xaxis)) {
					objCol.put("id", "");
					objCol.put("label", xaxis);
					objCol.put("type", "string");
					arrayCols.put(objCol);

				} else {
					objCol.put("id", "");
					objCol.put("label", name);
					objCol.put("type", "number");
					arrayCols.put(objCol);
				}
			}

			objDataTable.put("cols", arrayCols);

			JSONArray arrayDataResult = new JSONArray(con.getColumnResult(sql));
			for (int i = 0; i < arrayDataResult.length(); i++) {
				JSONObject objDataResult = arrayDataResult.getJSONObject(i);
				JSONObject objRow = new JSONObject();
				JSONArray arrayDataCol = new JSONArray();

				for (String name : columns) {
					JSONObject row = new JSONObject();
					if (name.equals(xaxis)) {
						String valuexAxis = objDataResult.getString(name);
						System.out.println("xaxis "+ xaxis);
						try {
							switch (name) {
							case "daykey":
								SimpleDateFormat fm = new SimpleDateFormat(
										"yyyy-MM-dd");

								valuexAxis = fm.parse(
										objDataResult.getString(name))
										.toString();

								break;
							case "weekkey":
								valuexAxis = "Tuần " + valuexAxis.substring(4) +"."+ valuexAxis.substring(0, 4)  ;
								break;
							case "quarterkey":
								valuexAxis = "Q" + valuexAxis.substring(4) +"."+ valuexAxis.substring(0, 4)  ;
								break;
							case "monthkey":
								System.out.println("monthkey ");
								valuexAxis = "T" + valuexAxis.substring(4) +"."+ valuexAxis.substring(0, 4)  ;
								break;
							case "halfkey":
								if(valuexAxis.substring(5) =="1")
									valuexAxis = "6T đầu."+ valuexAxis.substring(0, 4)  ;
								else
									valuexAxis = "6T sau."+ valuexAxis.substring(0, 4)  ;
								break;
							default:
								System.out.println("default ");
								break;
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						row.put("v", valuexAxis);
						arrayDataCol.put(row);
					}else{
						row.put("v", objDataResult.getString(name));
						arrayDataCol.put(row);	
					}
					
				}
				objRow.put("c", arrayDataCol);
				arrayRows.put(objRow);
			}
			objDataTable.put("rows", arrayRows);

			return objDataTable.toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("printStackTrace: ");
		}

		return "";
	}

	public String ENPOINT = "http://bsg.bkav.com:8113/SMSService.asmx?wsdl";

	public int sendSMS(String data, String phoneNumber) {
		SMSServiceStub stub;
		try {
			stub = new SMSServiceStub(ENPOINT);
			SendWithPriority param = new SendWithPriority();
			param.setContent(data);
			param.setTonumber(phoneNumber);
			SendWithPriorityResponse response = stub.sendWithPriority(param);
			return 0;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

	}

	@SuppressWarnings("unused")
	private final int _____BMM_____ = 0;

	public String getUserNameDept(String userDept) {
		SQLServerConnection conSQL = new SQLServerConnection();
		List<tbl_User> list = conSQL.getUser();
		for (tbl_User user : list) {
			if (user.getUser().equals(userDept))
				return user.getUserName();
			else
				continue;
		}
		return "Bộ trưởng";
	}

	public String getPhonenumberOfUserDept(String userDept) {
		SQLServerConnection conSQL = new SQLServerConnection();
		List<tbl_User> list = conSQL.getUser();
		for (tbl_User user : list) {
			if (user.getUser().equals(userDept))
				return user.getPhoneNumber();
			else
				continue;
		}
		return "0384600709";
	}

	public String getMissionReport(int missionId) {
		SQLServerConnection conSQL = new SQLServerConnection();
		List<MissionReport> list = conSQL.getMissionReport();
		for (MissionReport item : list) {
			if (missionId == Integer.valueOf(item.getMissionId())) {
				return item.getResult();
			} else
				continue;
		}
		return "";
	}

	public static String convertTypeTimeToDate(int typeTime, String data) {
		String stringDate = "Năm";
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date date;
		SimpleDateFormat dt1;
		try {
			date = dt.parse(data);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return stringDate;
		}
		switch (Integer.valueOf(typeTime)) {
		case 1:
			// *** same for the format String below
			dt1 = new SimpleDateFormat("dd/MM/yyyy");
			return dt1.format(date);
		case 2:
			// *** same for the format String below
			dt1 = new SimpleDateFormat("MM/yyyy");
			return dt1.format(date);
		case 3:
			// *** same for the format String below
			dt1 = new SimpleDateFormat("MM/yyyy");
			return dt1.format(date);
		case 4:
			// *** same for the format String below
			int dt2 = Integer.valueOf(new SimpleDateFormat("MM").format(date));

			dt1 = new SimpleDateFormat("yyyy");
			if (dt2 > 0 && dt2 < 4) {
				return " 1 - " + dt1.format(date);
			} else if (dt2 > 3 && dt2 < 7) {
				return " 2 - " + dt1.format(date);
			} else if (dt2 > 6 && dt2 < 10) {
				return " 3 - " + dt1.format(date);
			} else {
				return " 3 - " + dt1.format(date);
			}

		case 5:
			// *** same for the format String below
			dt1 = new SimpleDateFormat("yyyy");
			return dt1.format(date);

		default:
			break;
		}
		return stringDate;
	}

	public String getListSttcPagingForUser(String userId, int start, int max) {
		MySqlConnection con = new MySqlConnection();
		List<Statistical> list = con.getListPagingForUser(Statistical.class,
				start, max, userId);
		JSONArray arr = new JSONArray();
		DecimalFormat df2 = new DecimalFormat("###,###,###,###,###,###,###.##");
		try {
			for (Statistical sttc : list) {
				JSONObject json = new JSONObject();
				json.put("sttcId", sttc.getId());
				json.put("sttcName", sttc.getName());
				json.put("typeTime", sttc.getTypeTime());
				json.put("kpiValue", df2.format(sttc.getKpiValue()));
				if (sttc.getKpiState() == 0)
					json.put("kpiState", "Đạt");
				else
					json.put("kpiState", "Không đạt");
				json.put(
						"time",
						convertTypeTimeToDate(sttc.getTypeTime(),
								sttc.getTime()));
				json.put("realityValue", df2.format(sttc.getRealityValue()));
				json.put("unitOfRealityValue", sttc.getUnitOfRealityValue());
				arr.put(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("JSONException");
		}
		return arr.toString();
	}

	public List<String> keyJSONOBJ(JSONObject obj) {
		List<String> columnNames = new ArrayList<>();
		// obj = new org.json.JSONObject(obj);

		Iterator<?> keys = obj.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();

			columnNames.add(key);
		}
		return columnNames;
	}

	@SuppressWarnings("unused")
	private final int _____Dashboard_____ = 0;

	public String getDocumentByUser(User user, int fieldId) {
		MySqlConnection con = new MySqlConnection();
		List<Document> list = con.getListAll(Document.class);
		List<Group> listGrp = con.getListGroupByUserId(user.getUserId(),
				fieldId);
		JSONArray arr = new JSONArray();
		for (Group grp : listGrp) {
			JSONArray arrDoc = new JSONArray();
			JSONObject objDoc = new JSONObject();

			try {
				objDoc.put("groupId", grp.getGroupId());
				objDoc.put("groupName", grp.getGroupName());
				for (Document doc : list) {
					if (doc.getPermission() == user.getPermission()
							&& doc.getGrourpId() == grp.getGroupId()) {
						JSONObject obj = new JSONObject();
						obj.put("documentId", doc.getDocumentId());
						obj.put("documentName", doc.getDocumentName());
						obj.put("documentUrl", doc.getUrl());
						obj.put("documentDes", doc.getDescription());
						arrDoc.put(obj);
					}
				}
				objDoc.put("document", arrDoc);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("JSONException");
			}
			arr.put(objDoc);
		}

		return arr.toString();
	}

	public String getDashBoardDataOfDetailUser(User user, int fieldId) {
		JSONArray jsonArray = new JSONArray();
		List<String> configData = null;
		MySqlConnection con = new MySqlConnection();
		System.out.println("user.getUserId() " + user.getUserId() + " fieldId "
				+ fieldId);
		DashboardConfig dbcf = con
				.getDashboardConfig(user.getUserId(), fieldId);
		configData = readConfigData(dbcf.getConfigData());
		/*
		 * if (user.getPermission() == 1) configData = readConfigFile("admin",
		 * fielId); else configData = readConfigFile(user.getUserId(), fielId);
		 */

		try {
			String[] line1 = configData.get(0).split(" ");
			if (line1.length != 2) {
				System.out.println("line1: " + configData.get(0));
				return "";
			}
			int fieldIdDB = Integer.parseInt(line1[0]);
			Field field = con.get(Field.class, fieldIdDB);
			if (field == null) {
				System.out.println("fieldId: " + fieldIdDB);
				return "";
			}

			int numOfConfig = Integer.parseInt(line1[1]);

			if (numOfConfig != configData.size() - 1) {
				System.out.println("numOfConfig: " + numOfConfig);
				return "";
			}

			for (int i = 1; i <= numOfConfig; i++) {
				// DashBoardData ret = new DashBoardData();

				String config = configData.get(i);
				String[] configArr = config.split(" ");

				if (configArr.length != 3) {
					System.out.println("configArr.length: " + configArr.length);
					return "";
				}// tren 1 dong config luon co 3 phan tu
					// get nhom tuong ung trong db
				Group group = con.get(Group.class,
						Integer.parseInt(configArr[0]));

				if (group == null) {
					System.out.println("group null by Id: " + configArr[0]);
					return "";
				}

				if (group.getFieldId() != fieldId) {
					System.out.println("group.getFieldId(): "
							+ group.getFieldId());
					return "";
				}
				if (user.getPermission() == 1
						&& !group.getUserId().equals("admin")) {
					System.out.println("group.getGroupId(): "
							+ group.getGroupId() + " group.getGroupName(): "
							+ group.getGroupName());
					System.out.println("group.getUserId(): "
							+ group.getUserId());
					return "";
				}

				// Check so luong cau hinh thong ke trong dong config
				int numOfTK = Integer.parseInt(configArr[1]);
				String[] listTkId = configArr[2].split("\\.");
				if (listTkId.length != numOfTK)
					return "";
				if (numOfTK == 1 && listTkId.length == 0)
					listTkId[0] = configArr[2];

				// ret.setGroupId(group.getGroupId());
				// ret.setGroupName(group.getGroupName());

				for (int j = 0; j < numOfTK; j++) {
					int tkId = Integer.parseInt(listTkId[j]);
					Statistical sttc = con.get(Statistical.class, tkId);

					if (sttc == null) {
						System.out.println("Sttc null by Id: " + tkId);
						return "";
					}
					if (sttc.getGroupId() != group.getGroupId()) {
						System.out.println("kpi.getGroupId(): "
								+ sttc.getGroupId() + " group.getGroupId(): "
								+ group.getGroupId());
						return "";
					}
					if (sttc.getUserId().equals("admin")
							&& !group.getUserId().equals("admin")) {
						System.out.println("sttc.getUserId()"
								+ sttc.getUserId() + " group.getUserId(: "
								+ group.getUserId());
						return "";
					}
					// if (user.getPermission() == 1
					// && !sttc.getUserId().equals("admin")) {
					// System.out.println("sttc.getUserId() "
					// + sttc.getUserId());
					// return "";
					// }

					// System.out.println("sttc.state: " + sttc.getState());

					JSONObject json = new JSONObject();
					ChartObject chart = con
							.getChartObjectBySttcId(sttc.getId());
					// if(url.length() == 0) url = "mic";
					// if (chart != null) {
					try {
						DecimalFormat df2 = new DecimalFormat(
								"###,###,###,###,###,###,###.##");
						json.put("groupId", group.getGroupId());
						json.put("groupName", group.getGroupName());
						json.put("sttcId", sttc.getId());
						json.put("sttcName", sttc.getName());
						json.put("typeTime", sttc.getTypeTime());
						json.put("kpiValue", df2.format(sttc.getKpiValue()));
						if (sttc.getKpiState() == 0)
							json.put("kpiState", "Đạt");
						else
							json.put("kpiState", "Không đạt");
						json.put(
								"time",
								convertTypeTimeToDate(sttc.getTypeTime(),
										sttc.getTime()));
						json.put("realityValue",
								df2.format(sttc.getRealityValue()));
						json.put("unitOfRealityValue",
								sttc.getUnitOfRealityValue());
						json.put("fieldId", field.getId());
						json.put("fieldName", field.getName());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					jsonArray.put(json);
					// }

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Get List Statistical By UserId");
			return "";
		}

		return jsonArray.toString();
	}

	public List<String> getDashBoardData(User user, int fieldId, int groupID) {
		List<String> rs = new ArrayList<>();

		List<String> configData = null;
		MySqlConnection con = new MySqlConnection();
		System.out.println("user.getUserId(): " + user.getUserId());
		System.out.println("fieldId: " + fieldId);
		DashboardConfig dbcf = con
				.getDashboardConfig(user.getUserId(), fieldId);
		configData = readConfigData(dbcf.getConfigData());
		// if (user.getPermission() == 1)
		// configData = readConfigFile("admin", fielId);
		// else
		// configData = readConfigFile(user.getUserId(), fielId);
		// sysouser.getUserId()" + fieldId 2
		// System.out.println("user.getUserId()
		try {

			String[] line1 = configData.get(0).split(" ");
			if (line1.length != 2) {
				System.out.println("line1: " + configData.get(0));
				return new ArrayList<>();
			}
			int fieldIdDB = Integer.parseInt(line1[0]);
			Field field = con.get(Field.class, fieldIdDB);
			if (field == null) {
				System.out.println("fieldId: " + fieldIdDB);
				return new ArrayList<>();
			}

			int numOfConfig = Integer.parseInt(line1[1]);

			if (numOfConfig != configData.size() - 1) {
				System.out.println("numOfConfig: " + numOfConfig);
				return new ArrayList<>();
			}

			for (int i = 1; i <= numOfConfig; i++) {
				// DashBoardData ret = new DashBoardData();

				String config = configData.get(i);
				String[] configArr = config.split(" ");

				if (configArr.length != 3) {
					System.out.println("configArr.length: " + configArr.length);
					return new ArrayList<>();
				}// tren 1 dong config luon co 3 phan tu
					// get nhom tuong ung trong db
				Group group = con.get(Group.class,
						Integer.parseInt(configArr[0]));

				if (group == null) {
					System.out.println("group null by Id: " + configArr[0]);
					return new ArrayList<>();
				}

				if (group.getFieldId() != fieldId) {
					System.out.println("group.getFieldId(): "
							+ group.getFieldId());
					return new ArrayList<>();
				}

				if (user.getPermission() == 1
						&& !group.getUserId().equals("admin")) {
					System.out.println("group.getGroupId(): "
							+ group.getGroupId() + " group.getGroupName(): "
							+ group.getGroupName());
					System.out.println("group.getUserId(): "
							+ group.getUserId());
					return new ArrayList<>();
				}

				// Check so luong cau hinh thong ke trong dong config
				int numOfTK = Integer.parseInt(configArr[1]);
				String[] listTkId = configArr[2].split("\\.");
				if (listTkId.length != numOfTK)
					return new ArrayList<>();
				if (numOfTK == 1 && listTkId.length == 0)
					listTkId[0] = configArr[2];

				// ret.setGroupId(group.getGroupId());
				// ret.setGroupName(group.getGroupName());

				List<String> listJson = new ArrayList<>();
				for (int j = 0; j < numOfTK; j++) {
					int tkId = Integer.parseInt(listTkId[j]);
					Statistical sttc = con.get(Statistical.class, tkId);

					if (sttc == null) {
						System.out.println("Sttc null by Id: " + tkId);
						return new ArrayList<>();
					}
					if (sttc.getGroupId() != group.getGroupId()) {
						System.out.println("kpi.getGroupId(): "
								+ sttc.getGroupId() + " group.getGroupId(): "
								+ group.getGroupId());
						return new ArrayList<>();
					}
					if (sttc.getUserId().equals("admin")
							&& !group.getUserId().equals("admin")) {
						System.out.println("sttc.getUserId()"
								+ sttc.getUserId() + " group.getUserId(: "
								+ group.getUserId());
						return new ArrayList<>();
					}
					/*
					 * if (user.getPermission() == 1 &&
					 * !sttc.getUserId().equals("admin")) {
					 * System.out.println("sttc.getUserId() " +
					 * sttc.getUserId()); return new ArrayList<>(); }
					 */

					// System.out.println("sttc.state: " + sttc.getState());
					// {"kpiState":1,"kpiValue":0,"growthState":1,"unitOfRealityValue":"%","type":2,"progressValue":0,"growthValue":10,"typeTime":5,"name":"Quyết toán các nguồn vốn đầu tư thuộc ngân sách nhà nước","realityValue":10,"id":95,"time":"15/03/2018 12:00:00 SA","unitOfGrowthValue":"0"}
					JSONObject json = new JSONObject(sttc.toJSONString());
					// System.out.println("sttc.toJSONString() "+
					// sttc.toJSONString());
					/*
					 * ChartObject chart = con
					 * .getChartObjectBySttcId(sttc.getId());
					 */
					// if(url.length() == 0) url = "mic";
					DecimalFormat df2 = new DecimalFormat(
							"###,###,###,###,###,###,###.##");
					json.put(
							"time",
							convertTypeTimeToDate(
									Integer.valueOf(json.getString("typeTime")),
									json.getString("time")));
					// json.put("typeTime", "Năm");
					json.put("kpiValue", df2.format(Float.valueOf(json
							.getString("kpiValue"))));
					json.put("growthValue", df2.format(Float.valueOf(json
							.getString("growthValue"))));
					json.put("realityValue", df2.format(Float.valueOf(json
							.getString("realityValue"))));
					json.put("groupId", group.getGroupId());
					json.put("groupName", group.getGroupName());
					json.put("fieldId", field.getId());
					json.put("fieldName", field.getName());
					String url = "https://eformmic.hcdt.vn:9643/ui/DashboardChartServlet?id="
							+ sttc.getId() + "&userId=admin";
					/*
					 * if (chart != null) { try { json.put("url",
					 * chart.getUrlWebView()); } catch (JSONException e) { //
					 * TODO Auto-generated catch block e.printStackTrace(); }
					 * 
					 * } else { json.put("url",
					 * "https://mof.bkav.com:8002/error"); }
					 */
					json.put("url", url);
					String data = getFormulaChartbySttc(sttc.getId());
					json.put("data", data);
					listJson.add(json.toString());
				}

				if (groupID != 0) {// get dashboard by groupID
					if (groupID == Integer.parseInt(configArr[0])) {
						// ret.setJsonKPI(listJson);
						// rs.add(listJson);
						rs = listJson;
					}
				} else {
					// ret.setJsonKPI(listJson);
					// rs.add(ret);
					rs = listJson;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

		return rs;
	}

	// public List<String> getDescriptionOfBIColumn(int groupId, int period,
	// int areaType) {
	// MySqlConnection con = new MySqlConnection();
	// Group group = con.get(Group.class, groupId);
	// if (group == null)
	// new ArrayList<String>();
	// String listTb = group.getTableList();
	// if (listTb.length() == 0)
	// return new ArrayList<String>();
	// String[] listTable = listTb.split("\\.");
	// if (listTable.length == 0)
	// listTable[0] = listTb;
	// List<BIColumn> listColumn = new ArrayList<>();
	// for (String s : listTable) {
	// listColumn
	// .addAll(con.getListBIColumnByTableID(Integer.parseInt(s)));
	// }
	// List<String> rs = new ArrayList<>();
	// for (BIColumn col : listColumn) {
	// if (col.getDescription() != null
	// && col.getDescription().length() > 0) {
	// BITable table = con.get(BITable.class, col.getTableID());
	// if (table.getAreaType() != areaType
	// && table.getPeriod() != period)
	// continue;
	// JSONObject json = new JSONObject();
	//
	// try {
	// json.put("description", col.getDescription());
	// json.put("column", col.getColumnName());
	// json.put("table", table.getName());
	// json.put("TableId", table.getID());
	// json.put("ColumnId", col.getID());
	// rs.add(json.toString());
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }
	// return rs;
	// }

	@SuppressWarnings("unused")
	private final int _____BI_Data_____ = 0;

	public String getColumnResult(int sttcId) {
		MySqlConnection con = new MySqlConnection();
		List<Formula> list = con.getListAll(Formula.class);
		String sql = "";

		for (Formula formula : list) {
			if (formula.getSttcId() == sttcId)
				sql = formula.getSql();

		}

		String dataTable = con.getColumnResult(sql);
		try {
			JSONArray arr = new JSONArray(dataTable);
			for (int i = 0; i < arr.length(); i++) {
				if (i == 0) {
					JSONObject obj = new JSONObject(arr.get(i));
					List<String> nameColum = keyJSONOBJ(obj);

				}
			}

			// for()
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static String getDescriptionOfBIColumn(String jsonParam) {
		try {
			JSONObject jsonP = new JSONObject(jsonParam);
			int grp = 0;
			JSONArray arrP = null;
			if (jsonP.has("grp") && !jsonP.isNull("grp")) {
				grp = jsonP.getInt("grp");
			} else {
				return "";
			}

			if (jsonP.has("filter") && !jsonP.isNull("filter")) {
				arrP = jsonP.getJSONArray("filter");
				System.out.println(arrP.toString());
			}

			MySqlConnection con = new MySqlConnection();
			Group group = con.get(Group.class, grp);
			if (group == null)
				return "";
			String listTb = group.getTableList();
			// System.out.println(listTb);
			if (listTb.length() == 0)
				return "";
			String[] listTableID = listTb.split("\\.");
			if (listTableID.length == 0)
				listTableID[0] = listTb;
			List<BIColumn> listColumn = new ArrayList<>();
			for (String id : listTableID) {
				listColumn.addAll(con.getListBIColumnByTableID(Integer
						.parseInt(id)));
			}
			// System.out.println(listColumn.size());

			List<String> rs = new ArrayList<>();
			JSONArray arr = new JSONArray();
			for (BIColumn col : listColumn) {
				if (col.getDescription() != null
						&& col.getDescription().length() > 0) {
					BITable table = con.get(BITable.class, col.getTableID());

					/*
					 * if (table.getAreaType() != areaType || table.getPeriod()
					 * != period) continue;
					 */
					// TODO: check table truoc roi moi check colum.
					int check = 0;
					if (arrP != null) {
						JSONArray tableView = new JSONArray(table.getView());
						check = checkDBViewMatched(1, 1, tableView, arrP);
						if (check == -1) {
							continue;
						}
					}

					JSONObject json = new JSONObject();

					json.put("table", table.getName());
					json.put("TableId", table.getID());
					json.put("column", col.getColumnName());
					json.put("ColumnId", col.getID());
					json.put("type", 1);
					json.put("dataType", 2);
					JSONArray viewJson = new JSONArray(table.getView());
					json.put("group", viewJson);
					json.put("description", col.getDescription());
					if (check == 1) {
						// make column if
						JSONArray arrayIf = new JSONArray();
						JSONObject sumJson = new JSONObject();
						JSONObject avgJson = new JSONObject();
						JSONObject minJson = new JSONObject();
						JSONObject maxJson = new JSONObject();
						arrayIf.put(sumJson);
						arrayIf.put(avgJson);
						arrayIf.put(minJson);
						arrayIf.put(maxJson);
						sumJson.put("function", "sum");
						sumJson.put("description",
								"sum_" + col.getDescription());
						avgJson.put("function", "avg");
						avgJson.put("description",
								"avg_" + col.getDescription());
						minJson.put("function", "min");
						minJson.put("description",
								"min_" + col.getDescription());
						maxJson.put("function", "max");
						maxJson.put("description",
								"max_" + col.getDescription());

						json.put("columnif", arrayIf);
					}

					rs.add(json.toString());
					arr.put(json);
				}
			}

			// get formula
			if (arrP != null) {
				List<BIFormula> formulaList = con.getListAll(BIFormula.class);
				for (BIFormula formula : formulaList) {
					// check view and check filter
					JSONObject jsonValue = new JSONObject(
							formula.getJsonValue());
					JSONObject jsonView = new JSONObject(formula.getView());
					JSONArray formulaView = jsonView.getJSONObject("view")
							.getJSONArray("filter");
					int jsonCheck = checkDBViewMatched(2, 2, formulaView, arrP);

					JSONObject formulaObject = new JSONObject();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("data",
							new JSONObject(formula.getJsonValue()));
					// jsonObject.put("view", new
					// JSONObject(formula.getView()));
					jsonObject.put("view", jsonView.getJSONObject("view"));
					formulaObject.put("ColumnId", formula.getID());
					formulaObject.put("jsonObject", jsonObject);
					formulaObject.put("description", formula.getName());

					// formulaObject.put("sql", formula.getSQL());
					if (jsonCheck == 0) {
						arr.put(formulaObject);
					}
				}
			}
			System.out.println("arr size: " + arr.length());
			return arr.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public RetValData getFilter() {
		MySqlConnection con = new MySqlConnection();

		List<BIViewGroup> biViewGroupList = con.getListAll(BIViewGroup.class);
		JSONArray filterArray = new JSONArray();
		try {
			for (BIViewGroup biViewGroup : biViewGroupList) {
				JSONObject biViewGroupJson = new JSONObject();
				biViewGroupJson.put("viewGroupId", biViewGroup.getID());
				biViewGroupJson
						.put("description", biViewGroup.getDescription());
				biViewGroupJson.put("name", biViewGroup.getName());

				JSONArray data = new JSONArray();
				List<BIView> biViewList = con.getListAll(BIView.class);
				for (BIView biView : biViewList) {
					if (biView.getGroupID() == biViewGroup.getID()) {
						JSONObject biViewJson = new JSONObject();
						biViewJson.put("ColumnId", biView.getID());
						biViewJson.put("Name", biView.getName());
						biViewJson.put("Code", biView.getCode());
						biViewJson.put("ParentCode", biView.getParentCode());
						biViewJson.put("Level", biView.getLevel());
						biViewJson.put("NameInDB", biView.getNameInDB());
						if (biView.getDescription() == null) {
							biViewJson.put("Description", "");
						} else {
							biViewJson.put("Description",
									biView.getDescription());
						}

						data.put(biViewJson);
					}
				}
				biViewGroupJson.put("data", data);
				filterArray.put(biViewGroupJson);
			}
			return new RetValData(filterArray.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			return new RetValData(ErrorCode.Invalid_Input.code);
		}
	}

	public String getSQLFromJSONObject(String jsonString) {
		// TODO : xu ly date, reginal
		MySqlConnection con = new MySqlConnection();
		String sql = "";
		try {
			JSONObject json = new JSONObject(jsonString);
			String selectData = json.getString("selectdata");

			JSONObject jsonTimes = json.getJSONObject("time");
			String startDate = jsonTimes.getString("startDate");
			String endDaate = jsonTimes.getString("endDate");

			JSONObject jsonCycle = json.getJSONObject("cycle");
			String typeTime = jsonCycle.getString("typeTime");
			String cycleTimes = jsonCycle.getString("cycleTimes");

			JSONObject jsonAddress = json.getJSONObject("address");
			int regionalId = Integer.parseInt(jsonAddress
					.getString("regionalId"));

			JSONArray jsonsCondition = json.getJSONArray("condition");
			List<String> listConditions = new ArrayList<String>();
			List<String> listTables = new ArrayList<String>();
			for (int i = 0; i < jsonsCondition.length(); i++) {
				JSONObject jsonObj = jsonsCondition.getJSONObject(i);
				String value = jsonObj.getString("value");
				String operator = jsonObj.getString("operator");
				int tableId = Integer.parseInt(jsonObj.getString("tableId"));
				String columnName = jsonObj.getString("columnName");

				BITable table = con.get(BITable.class, tableId);
				listTables.add(table.getName());

				if (value.length() > 0) {
					String condition = columnName + " " + operator + " "
							+ value;
					listConditions.add(condition);
				} else {
					int idAdvancedFormula = Integer.parseInt(columnName);
					AdvancedFormula adFor = con.get(AdvancedFormula.class,
							idAdvancedFormula);
					int formulaId = adFor.getFormulaId();
					Formula formula = con.get(Formula.class, formulaId);
					String condition = formula.getSql();
					String[] arrCheck = condition.split(" ");
					for (String s : arrCheck) {
						if (s.equalsIgnoreCase("select")// prevent sql injection
								|| s.equalsIgnoreCase("from")
								|| s.equalsIgnoreCase("update")
								|| s.equalsIgnoreCase("delete")
								|| s.equalsIgnoreCase("alter")
								|| s.equalsIgnoreCase("grant")
								|| s.equalsIgnoreCase("drop")
								|| s.equalsIgnoreCase("where"))
							return "";
					}
					listConditions.add(condition);
				}
			}

			sql = "select ";
			String[] listTableId = selectData.split("\\.");
			for (int i = 0; i < listTableId.length; i++) {
				int id = Integer.parseInt(listTableId[i]);
				BIColumn col = con.get(BIColumn.class, id);
				if (i == listTableId.length - 1)
					sql += col.getColumnName() + " ";
				else
					sql += col.getColumnName() + ", ";
			}

			sql += "from";

			for (int i = 0; i < listTables.size(); i++) {
				if (i == listTables.size() - 1)
					sql = sql + " " + listTables.get(i) + " ";
				else
					sql = sql + " " + listTables.get(i) + ", ";
			}

			sql += "where ";

			for (int i = 0; i < listConditions.size(); i++) {
				if (i == listConditions.size() - 1)
					sql = sql + " " + listConditions.get(i) + ";";
				else
					sql = sql + " " + listConditions.get(i) + ", ";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}

	@SuppressWarnings("unused")
	private final int _____Dashboard_Config_____ = 0;

	private boolean checkConfigFile(String base64DataConfig, User user,
			int fieldId) // Ham kiem tra tinh dung dan cua
	// file config
	{
		List<String> configData = readConfigData(base64DataConfig);
		try {
			MySqlConnection con = new MySqlConnection();

			int numOfConfig = Integer.parseInt(configData.get(0));

			if (numOfConfig != configData.size() - 1) {
				System.out.println("numOfConfig: " + numOfConfig);
				return false;
			}

			for (int i = 1; i <= numOfConfig; i++) {
				// DashBoardData ret = new DashBoardData();

				String config = configData.get(i);
				String[] configArr = config.split(" ");

				if (configArr.length != 3) {
					// System.out.println("configArr.length: " +
					// configArr.length);
					return false;
				}// tren 1 dong config luon co 3 phan tu
					// get nhom tuong ung trong db
				Group group = con.get(Group.class,
						Integer.parseInt(configArr[0]));

				if (group == null) {
					System.out.println("group null by Id: " + configArr[0]);
					return false;
				}

				if (group.getFieldId() != fieldId) {
					System.out.println("group.getFieldId(): "
							+ group.getFieldId());
					return false;
				}

				if (user.getPermission() == 1
						&& !group.getUserId().equals("admin")) {
					System.out.println("group.getUserId(): "
							+ group.getUserId());
					return false;
				}

				// Check so luong cau hinh thong ke trong dong config
				int numOfTK = Integer.parseInt(configArr[1]);
				if (numOfTK == 0 && Integer.parseInt(configArr[2]) == 0)
					continue;

				String[] listTkId = configArr[2].split("\\.");
				if (listTkId.length != numOfTK)
					return false;//
				if (numOfTK == 1 && listTkId.length == 0)
					listTkId[0] = configArr[2];
				for (int j = 0; j < numOfTK; j++) {
					int tkId = Integer.parseInt(listTkId[j]);
					Statistical kpi = con.get(Statistical.class, tkId);

					if (kpi == null) {
						System.out.println("Sttc null by Id: " + tkId);
						return false;
					}
					if (kpi.getGroupId() != group.getGroupId()) {
						System.out.println("kpi error: " + kpi.getId());
						System.out.println("kpi.getGroupId(): "
								+ kpi.getGroupId() + " group.getGroupId(): "
								+ group.getGroupId());
						return false;
					}

					if (user.getPermission() == 1
							&& !kpi.getUserId().equals("admin")) {
						System.out.println("kpi.getUserId(): "
								+ kpi.getUserId());
						return false;
					}

					if (kpi.getUserId().equals("admin")
							&& !group.getUserId().equals("admin")) {
						System.out.println("kpi.getUserId(): "
								+ kpi.getUserId() + " group.getUserId(): "
								+ group.getUserId());
						return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// private boolean checkConfigFile(User user, int fieldIds) // Ham kiem tra
	// tinh dung dan cua
	// // file config
	// {
	// List<String> configData = readConfigFile(user.getUserId(), fieldIds);
	// try {
	// MySqlConnection con = new MySqlConnection();
	// String[] line1 = configData.get(0).split(" ");
	// if(line1.length != 2)
	// {
	// System.out.println("line1: " + configData.get(0));
	// return false;
	// }
	// int fieldId = Integer.parseInt(line1[0]);
	// Field field = con.get(Field.class, fieldId);
	// if(field == null)
	// {
	// System.out.println("fieldId: " + fieldId);
	// return false;
	// }
	//
	// int numOfConfig = Integer.parseInt(line1[1]);
	//
	//
	// if (numOfConfig != configData.size() - 1) {
	// System.out.println("numOfConfig: " + numOfConfig);
	// return false;
	// }
	//
	// for (int i = 1; i <= numOfConfig; i++) {
	// // DashBoardData ret = new DashBoardData();
	//
	// String config = configData.get(i);
	// String[] configArr = config.split(" ");
	//
	// if (configArr.length != 3) {
	// System.out.println("configArr.length: " + configArr.length);
	// return false;
	// }// tren 1 dong config luon co 3 phan tu
	// // get nhom tuong ung trong db
	// Group group = con.get(Group.class,
	// Integer.parseInt(configArr[0]));
	//
	// if (group == null) {
	// System.out.println("group null by Id: " + configArr[0]);
	// return false;
	// }
	//
	// if(group.getFieldId() != fieldId)
	// {
	// System.out.println("group.getFieldId(): " + group.getFieldId());
	// return false;
	// }
	//
	// if (user.getPermission() == 1
	// && !group.getUserId().equals("admin")) {
	// System.out.println("group.getUserId(): "
	// + group.getUserId());
	// return false;
	// }
	//
	// // Check so luong cau hinh thong ke trong dong config
	// int numOfTK = Integer.parseInt(configArr[1]);
	// if (numOfTK == 0 && Integer.parseInt(configArr[2]) == 0)
	// continue;
	//
	// String[] listTkId = configArr[2].split("\\.");
	// if (listTkId.length != numOfTK)
	// return false;//
	// if (numOfTK == 1 && listTkId.length == 0)
	// listTkId[0] = configArr[2];
	// for (int j = 0; j < numOfTK; j++) {
	// int tkId = Integer.parseInt(listTkId[j]);
	// Statistical kpi = con.get(Statistical.class, tkId);
	//
	// if (kpi == null) {
	// System.out.println("Sttc null by Id: " + tkId);
	// return false;
	// }
	// if (kpi.getGroupId() != group.getGroupId()) {
	// System.out.println("kpi error: " + kpi.getId());
	// System.out.println("kpi.getGroupId(): "
	// + kpi.getGroupId() + " group.getGroupId(): "
	// + group.getGroupId());
	// return false;
	// }
	//
	// if (user.getPermission() == 1
	// && !kpi.getUserId().equals("admin")) {
	// System.out.println("kpi.getUserId(): "
	// + kpi.getUserId());
	// return false;
	// }
	//
	// if (kpi.getUserId().equals("admin")
	// && !group.getUserId().equals("admin")) {
	// System.out.println("kpi.getUserId(): "
	// + kpi.getUserId() + " group.getUserId(): "
	// + group.getUserId());
	// return false;
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// return true;
	// }

	// public int uploadConfigFile(User user, String base64ConfigData, int
	// fieldId) {
	// File confFile = new File(configPath + user.getUserId() + ".conf");
	// byte[] bkData = null; // Data backup old config
	// if (confFile.exists()) {
	// if (user.getPermission() == 1)
	// bkData = Utility.readFileToByte(configPath + "admin.conf");
	// else
	// bkData = Utility.readFileToByte(configPath + user.getUserId()
	// + ".conf");
	// confFile.delete();
	// }
	// if (user.getPermission() == 1)
	// Utility.writeBytesToFile(
	// Base64Utils.base64Decode(base64ConfigData), configPath
	// + "admin.conf");
	// else
	// Utility.writeBytesToFile(
	// Base64Utils.base64Decode(base64ConfigData), configPath
	// + user.getUserId() + ".conf");
	//
	// if (confFile.exists()) {
	// if (checkConfigFile(user, fieldId)) {
	// if (user.getPermission() == 1) // User la admin, cap nhat lai
	// // config cua tat ca user
	// {
	// List<String> adminConfig = readConfigFile("admin", fieldId);
	// System.out.println("adminConfig.size(): "
	// + adminConfig.size());
	// MySqlConnection con = new MySqlConnection();
	// List<User> listUser = con.getListAll(User.class);
	// for (User u : listUser) {
	// if (u.getPermission() == 1)
	// continue; // Chi cap nhat cho user thuong
	// File f = new File(configPath + u.getUserId() + ".conf");
	// if (!f.exists()) // File config cua user ko ton tai ->
	// // lay luon file config moi
	// {
	// Utility.writeBytesToFile(
	// Base64Utils.base64Decode(base64ConfigData),
	// configPath + u.getUserId() + ".conf");
	// continue;
	// }
	// byte[] bkUserConfig = Utility.readFileToByte(configPath
	// + u.getUserId() + ".conf"); // Data backup old
	// // user's
	// // config
	// f.delete();
	// List<String> userConfig = readConfigFile(u.getUserId(), fieldId); // Đọc
	// // từng
	// // dòng
	// // config
	// // của
	// // user
	// List<String> newUserConfigTmp = new ArrayList<>(); // Biến
	// // lưu
	// // config
	// // mới
	// List<String> newUserConfig = new ArrayList<>();
	// for (int i = 1; i < userConfig.size(); i++) // Đọc từng
	// // dòng
	// // config
	// // của user
	// {
	// String[] arrConfig = userConfig.get(i).split(" ");
	// int groupId = Integer.parseInt(arrConfig[0]);
	// Group group = con.get(Group.class, groupId);
	// if (group == null)
	// continue; // Nhóm không tồn tại -> không lưu
	// if (!group.getUserId().equals("admin")) {// Nhóm của
	// // user
	// // tạo
	// // ->
	// // lưu
	// // trực
	// // tiếp
	// newUserConfigTmp.add(userConfig.get(i));
	// continue;
	// }
	//
	// String[] sttcUserConfig = arrConfig[2].split("\\.");// Biến
	// // chứa
	// // danh
	// // sách
	// // id
	// // của
	// // thống
	// // kê
	// String newSttcUserConfig = "";
	// for (int j = 0; j < sttcUserConfig.length; j++) {
	// int sttcId = Integer
	// .parseInt(sttcUserConfig[j]);
	// Statistical sttc = con.get(Statistical.class,
	// sttcId);
	// if (sttc == null)
	// continue;
	// if (!sttc.getUserId().equals("admin"))
	// newSttcUserConfig = newSttcUserConfig
	// + sttcUserConfig[j] + "."; // tạo
	// // biến
	// // tạm,
	// // lưu
	// // config
	// // thống
	// // kê
	// // của
	// // user
	// }
	// String sttcAdminConfig = "";
	// for (int j = 1; j < adminConfig.size(); j++) // Lấy
	// // config
	// // thống
	// // kê
	// // mới
	// // của
	// // admin
	// {
	// String[] adminConfigArr = adminConfig.get(j)
	// .split(" ");
	// int grTmp = Integer.parseInt(adminConfigArr[0]);
	// if (grTmp == groupId) {
	// sttcAdminConfig = adminConfigArr[2];
	// break;
	// }
	// }
	//
	// if (sttcAdminConfig.length() == 0)
	// continue; // group không tồn tại -> không lưu
	// String newSttcConfig = newSttcUserConfig
	// + sttcAdminConfig;
	// int newSttcLength = newSttcConfig.split("\\.").length;
	// String newConfigLine = arrConfig[0] + " "
	// + Integer.toString(newSttcLength) + " "
	// + newSttcConfig; // dòng config mới
	// newUserConfigTmp.add(newConfigLine);
	// }
	//
	// newUserConfig = newUserConfigTmp;
	// // Xứ lý các nhóm mới do user thêm
	// for (int i = 1; i < adminConfig.size(); i++) {
	// String groupAdminId = adminConfig.get(i).split(" ")[0];
	// boolean check = false;
	// for (int j = 0; j < newUserConfigTmp.size(); j++) {
	// String groupUserId = newUserConfigTmp.get(j)
	// .split(" ")[0];
	// if (groupAdminId.equals(groupUserId)) {
	// check = true;
	// break;
	// }
	// }
	// if (!check)
	// newUserConfig.add(adminConfig.get(i));
	// }
	//
	// boolean check = writeConfigFile(newUserConfig,
	// u.getUserId());
	//
	// if (!check || !checkConfigFile(u, fieldId))
	// Utility.writeBytesToFile(bkUserConfig, configPath
	// + u.getUserId() + ".conf");
	// }
	// }
	// return ErrorCode.OK.code;
	// }
	// }
	// if (user.getPermission() == 1)
	// Utility.writeBytesToFile(bkData, configPath + "admin.conf");
	// else
	// Utility.writeBytesToFile(bkData, configPath + user.getUserId()
	// + ".conf");
	// return ErrorCode.ConfigFile_Not_In_Format.code;
	// }

	public List<String> readConfigData(String base64DataConfig) {
		BufferedReader reader = null;
		List<String> rs = new ArrayList<>();
		try {
			byte[] initialArray = Base64Utils.base64Decode(base64DataConfig);
			Reader targetReader = new StringReader(new String(initialArray));
			reader = new BufferedReader(targetReader);
			String line = reader.readLine();
			while (line != null) {
				rs.add(line);
				// System.out.println(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public int uploadConfigFile(User user, String base64ConfigData, int fieldId) {
		MySqlConnection con = new MySqlConnection();
		if (checkConfigFile(base64ConfigData, user, fieldId)) {
			DashboardConfig config = con.getDashboardConfig(user.getUserId(),
					fieldId);
			config.setConfigData(base64ConfigData);
			con.update(DashboardConfig.class, config);
		} else
			return ErrorCode.ConfigFile_Not_In_Format.code;

		if (user.getPermission() == 1) // User la admin, cap nhat lai
										// config cua tat ca user
		{
			List<String> adminConfig = readConfigData(base64ConfigData);
			System.out.println("adminConfig.size(): " + adminConfig.size());

			List<User> listUser = con.getListAll(User.class);
			for (User u : listUser) {
				if (u.getPermission() == 1)
					continue; // Chi cap nhat cho user thuong
				DashboardConfig currentUserConfig = con.getDashboardConfig(
						u.getUserId(), fieldId);
				if (currentUserConfig == null) // File config cua user ko ton
												// tai ->
				// lay luon file config moi
				{
					DashboardConfig newConfig = new DashboardConfig();
					newConfig.setConfigData(base64ConfigData);
					newConfig.setFieldId(fieldId);
					newConfig.setUserId(u.getUserId());
					con.insert(newConfig);
					continue;
				}

				List<String> userConfig = readConfigData(currentUserConfig
						.getConfigData()); // Đọc
				// từng
				// dòng
				// config
				// của
				// user
				List<String> newUserConfigTmp = new ArrayList<>(); // Biến
																	// lưu
																	// config
																	// mới
				List<String> newUserConfig = new ArrayList<>();
				for (int i = 1; i < userConfig.size(); i++) // Đọc từng
															// dòng
															// config
															// của user
				{
					String[] arrConfig = userConfig.get(i).split(" ");
					int groupId = Integer.parseInt(arrConfig[0]);
					Group group = con.get(Group.class, groupId);
					if (group == null)
						continue; // Nhóm không tồn tại -> không lưu
					if (!group.getUserId().equals("admin")) {// Nhóm của
																// user
																// tạo
																// ->
																// lưu
																// trực
																// tiếp
						newUserConfigTmp.add(userConfig.get(i));
						continue;
					}

					String[] sttcUserConfig = arrConfig[2].split("\\.");// Biến
																		// chứa
																		// danh
																		// sách
																		// id
																		// của
																		// thống
																		// kê
					String newSttcUserConfig = "";
					for (int j = 0; j < sttcUserConfig.length; j++) {
						int sttcId = Integer.parseInt(sttcUserConfig[j]);
						Statistical sttc = con.get(Statistical.class, sttcId);
						if (sttc == null)
							continue;
						if (!sttc.getUserId().equals("admin"))
							newSttcUserConfig = newSttcUserConfig
									+ sttcUserConfig[j] + "."; // tạo
																// biến
																// tạm,
																// lưu
																// config
																// thống
																// kê
																// của
																// user
					}
					String sttcAdminConfig = "";
					for (int j = 1; j < adminConfig.size(); j++) // Lấy
																	// config
																	// thống
																	// kê
																	// mới
																	// của
																	// admin
					{
						String[] adminConfigArr = adminConfig.get(j).split(" ");
						int grTmp = Integer.parseInt(adminConfigArr[0]);
						if (grTmp == groupId) {
							sttcAdminConfig = adminConfigArr[2];
							break;
						}
					}

					if (sttcAdminConfig.length() == 0)
						continue; // group không tồn tại -> không lưu
					String newSttcConfig = newSttcUserConfig + sttcAdminConfig;
					int newSttcLength = newSttcConfig.split("\\.").length;
					String newConfigLine = arrConfig[0] + " "
							+ Integer.toString(newSttcLength) + " "
							+ newSttcConfig; // dòng config mới
					newUserConfigTmp.add(newConfigLine);
				}

				newUserConfig = newUserConfigTmp;
				// Xứ lý các nhóm mới do user thêm
				for (int i = 1; i < adminConfig.size(); i++) {
					String groupAdminId = adminConfig.get(i).split(" ")[0];
					boolean check = false;
					for (int j = 0; j < newUserConfigTmp.size(); j++) {
						String groupUserId = newUserConfigTmp.get(j).split(" ")[0];
						if (groupAdminId.equals(groupUserId)) {
							check = true;
							break;
						}
					}
					if (!check)
						newUserConfig.add(adminConfig.get(i));
				}
				writeConfigFile(newUserConfig, currentUserConfig);
			}
		}
		return ErrorCode.OK.code;

	}

	private boolean writeConfigFile(List<String> list,
			DashboardConfig currentConfig) {
		System.out.println("list.size: " + list.size());
		String dataConfig = "";
		for (String s : list) {
			dataConfig = dataConfig + s + "\n";
		}
		String base64Config = Base64Utils.base64Encode(dataConfig.getBytes());
		currentConfig.setConfigData(base64Config);
		MySqlConnection con = new MySqlConnection();
		if (con.update(DashboardConfig.class, currentConfig) == ErrorCode.OK.code)
			return true;
		return false;
	}

	private boolean writeConfigFile(List<String> list, String userId) {
		System.out.println("list.size: " + list.size());
		try {
			File file = new File(configPath + userId + ".conf");
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			pw.write(list.size() + "\n");
			for (int i = 0; i < list.size(); i++)
				pw.write(list.get(i) + "\n");
			pw.close();
			File f = new File(configPath + userId + ".conf");
			if (f.exists())
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private static List<String> readConfigFile(String userId, int fieldID) {
		BufferedReader reader;
		List<String> rs = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(configPath + userId
					+ "_" + fieldID + ".conf"));
			String line = reader.readLine();
			while (line != null) {
				rs.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rs;
	}

	// add wg
	/*
	 * public int addWidget_Resource(int type, String widgetId, String
	 * queryDataConfig, int typeTime, String viewTableName, List<String>
	 * columnNames, int sttcId, String yAxisLabel) { // String configString =
	 * createWidgetConfig(type, widgetId, // queryDataConfig, typeTime,
	 * viewTableName, "MIC_DASHBOARD_DB", // columnNames); String configString =
	 * createWidgetConfig(type, widgetId, queryDataConfig, typeTime,
	 * viewTableName, "MIC_DASHBOARD_DB", columnNames, yAxisLabel);
	 * System.out.println("configString: " + configString); Widget_Resource wg =
	 * new Widget_Resource(); wg.setWIDGET_CONFIGS(configString);
	 * wg.setWIDGET_ID(widgetId); wg.setWIDGET_NAME(widgetId); MySqlConnection
	 * con = new MySqlConnection(); if (con.insertInWso2(wg) !=
	 * ErrorCode.OK.code) return ErrorCode.Not_OK.code;
	 * 
	 * Statistical_Widget sttcWg = new Statistical_Widget();
	 * sttcWg.setStatisticalId(sttcId); sttcWg.setWIDGET_ID(widgetId);
	 * sttcWg.setWIDGET_NAME(widgetId);
	 * 
	 * System.out.println("name of url: " + sttcWg.getWIDGET_NAME());
	 * 
	 * return con.insert(sttcWg); }
	 * 
	 * // public static String createWidgetConfig(int type, String widgetId, //
	 * String queryDataConfig,int typeTime, String viewTableName, List<String>
	 * // metaData) { // // if (widgetId == null) { // return "0"; // } else {
	 * // System.out.println("Data config: " + widgetId); // try { // String
	 * time = "nam"; // String timeNote = "Năm"; // String typeWidget = "bar";
	 * // switch (type) { // case 2: // typeWidget = "line"; // break; // case
	 * 3: // typeWidget = "area"; // break; // default: // break; // } // switch
	 * (typeTime) { // case 1: // time= "ngay"; // timeNote = "Ngày"; // break;
	 * // case 2: // time= "tuan"; // timeNote ="Tuần"; // break; // case 3: //
	 * time= "thang"; // timeNote = "Tháng"; // break; // case 4: // time=
	 * "quy"; // timeNote = "Quý"; // break; // default: // break; // } //
	 * String defaultConfig = //
	 * "{\"chartConfig\":{\"charts\":[],\"legend\":true,\"x\":\"\",\"xAxisLabel\":\"\",\"maxLength\":12,\"legendOrientation\":\"bottom\"},\"name\":\"\",\"id\":\"\",\"providerConfig\":{\"configs\":{\"type\":\"RDBMSBatchDataProvider\",\"config\":{\"purgingInterval\":60,\"publishingInterval\":1,\"publishingLimit\":30,\"purgingLimit\":30,\"isPurgingEnable\":false,\"queryData\":{\"queryFunction\":\"this.getQuery = function (username){return \\\"\\\" ;}\",\"query\":\"\",\"customWidgetInputs\":[],\"incrementalColumn\":\"\",\"timeColumns\":\"\",\"systemWidgetInputs\":[{\"defaultValue\":\"admin\",\"name\":\"username\"}]},\"datasourceName\":\"WSO2_DASHBOARD_DB2\",\"incrementalColumn\":\"\",\"timeColumns\":\"\",\"tableName\":\"\"}}},\"version\":\"1.0.0\",\"pubsub\":{\"types\":[]}}"
	 * ; // JSONObject configParser = new JSONObject(defaultConfig); //
	 * configParser.put("name", widgetId); // configParser.put("id", widgetId);
	 * // // JSONObject chartConfig = configParser.getJSONObject("chartConfig");
	 * // // chartConfig.put("x", ""); // chartConfig.put("x", time); //
	 * chartConfig.put("xAxisLabel", timeNote); // JSONArray chartsObj =
	 * chartConfig.getJSONArray("charts"); // for (int i = 0; i <
	 * metaData.size(); i++) { // JSONObject obj = new JSONObject(); //
	 * obj.put("type", typeWidget); // obj.put("y", metaData.get(i)); // //
	 * obj.put("color", metaData.get(i)); // chartsObj.put(obj); // } //
	 * JSONObject providerConfig = configParser //
	 * .getJSONObject("providerConfig"); // JSONObject config =
	 * providerConfig.getJSONObject("configs"); // JSONObject configQuery =
	 * config.getJSONObject("config"); // configQuery.put("incrementalColumn",
	 * time); // configQuery.put("timeColumns",time); //
	 * configQuery.put("tableName", viewTableName); // // JSONObject queryData =
	 * configQuery.getJSONObject("queryData"); // // queryData.put("query",
	 * queryDataConfig); // queryData.put("incrementalColumn", time); //
	 * queryData.put("timeColumns", time); // // String queryFunction =
	 * "this.getQuery = function (username){return \"" // + queryDataConfig +
	 * "\" ;}"; // queryData.put("queryFunction", queryFunction); //
	 * System.out.println(configParser); // return configParser.toString(); //
	 * // } catch (JSONException e) { // // TODO Auto-generated catch block //
	 * e.printStackTrace(); // return "-1"; // } // } // }
	 * 
	 * // 20190518 new function by vietpdb // public static String
	 * createWidgetConfig(int type, String widgetId, // String queryDataConfig,
	 * int typeTime, String viewTableName, String // dbSource, // List<String>
	 * metaData) { // // if (widgetId == null) { // return "0"; // } else { //
	 * System.out.println("Data config: " + widgetId); // try { // String time =
	 * "nam"; // String timeNote = "Năm"; // String typeWidget = "bar"; //
	 * switch (type) { // case 2: // typeWidget = "line"; // break; // case 3:
	 * // typeWidget = "area"; // case 4: // typeWidget = "arc"; // break; //
	 * default: // break; // } // switch (typeTime) { // case 1: // time =
	 * "ngay"; // timeNote = "Ngày"; // break; // case 2: // time = "tuan"; //
	 * timeNote = "Tuần"; // break; // case 3: // time = "thang"; // timeNote =
	 * "Tháng"; // break; // case 4: // time = "quy"; // timeNote = "Quý"; //
	 * break; // default: // break; // } // String defaultConfig = //
	 * "{\"chartConfig\":{\"charts\":[],\"legend\":true,\"xAxisLabel\":\"\",\"maxLength\":12,\"legendOrientation\":\"bottom\"},\"name\":\"\",\"id\":\"\",\"providerConfig\":{\"configs\":{\"type\":\"RDBMSBatchDataProvider\",\"config\":{\"purgingInterval\":60,\"publishingInterval\":1,\"publishingLimit\":30,\"purgingLimit\":30,\"isPurgingEnable\":false,\"queryData\":{\"queryFunction\":\"this.getQuery = function (username){return \\\"\\\" ;}\",\"query\":\"\",\"customWidgetInputs\":[],\"incrementalColumn\":\"\",\"timeColumns\":\"\",\"systemWidgetInputs\":[{\"defaultValue\":\"admin\",\"name\":\"username\"}]},\"datasourceName\":\"WSO2_DASHBOARD_DB2\",\"incrementalColumn\":\"\",\"timeColumns\":\"\",\"tableName\":\"\"}}},\"version\":\"1.0.0\",\"pubsub\":{\"types\":[]}}"
	 * ; // JSONObject configParser = new JSONObject(defaultConfig); //
	 * configParser.put("name", widgetId); // configParser.put("id", widgetId);
	 * // // JSONObject chartConfig = configParser //
	 * .getJSONObject("chartConfig"); // // // chartConfig.put("x", ""); // //
	 * chartConfig.put("xAxisLabel", timeNote); // JSONArray chartsObj =
	 * chartConfig.getJSONArray("charts"); // if (type == 4) { // JSONObject
	 * objConfigPie = new JSONObject(); // objConfigPie.put("x",
	 * metaData.get(1)); // objConfigPie.put("type", typeWidget); //
	 * objConfigPie.put("color", metaData.get(0)); // objConfigPie.put("mode",
	 * "pie"); // chartsObj.put(objConfigPie); // } else { //
	 * chartConfig.put("x", time); // for (int i = 0; i < metaData.size(); i++)
	 * { // JSONObject obj = new JSONObject(); // obj.put("type", typeWidget);
	 * // obj.put("y", metaData.get(i)); // // obj.put("color",
	 * metaData.get(i)); // chartsObj.put(obj); // } // } // // JSONObject
	 * providerConfig = configParser // .getJSONObject("providerConfig"); //
	 * JSONObject config = providerConfig.getJSONObject("configs"); //
	 * JSONObject configQuery = config.getJSONObject("config"); //
	 * configQuery.put("incrementalColumn", time); //
	 * configQuery.put("timeColumns", time); // configQuery.put("tableName",
	 * viewTableName); // //tungvtd add dbsource //
	 * configQuery.put("datasourceName", dbSource); // // JSONObject queryData =
	 * configQuery.getJSONObject("queryData"); // // queryData.put("query",
	 * queryDataConfig); // queryData.put("incrementalColumn", time); //
	 * queryData.put("timeColumns", time); // // String queryFunction =
	 * "this.getQuery = function (username){return \"" // + queryDataConfig +
	 * "\" ;}"; // queryData.put("queryFunction", queryFunction); //
	 * System.out.println(configParser); // return configParser.toString(); //
	 * // } catch (JSONException e) { // // TODO Auto-generated catch block //
	 * e.printStackTrace(); // return "-1"; // } // } // }
	 * 
	 * // 20190531 vietpdb add function public static String
	 * createWidgetConfig(int type, String widgetId, String queryDataConfig, int
	 * typeTime, String viewTableName, String dbSource, List<String> metaData,
	 * String yAxisLabel) {
	 * 
	 * if (widgetId == null) { return "0"; } else {
	 * System.out.println("Data config: " + widgetId); try { String time =
	 * "nam"; String timeNote = "Năm"; String typeWidget = "bar"; switch (type)
	 * { case 2: typeWidget = "line"; break; case 3: typeWidget = "area"; case
	 * 4: typeWidget = "arc"; break; default: break; } switch (typeTime) { case
	 * 1: time = "ngay"; timeNote = "Ngày"; break; case 2: time = "tuan";
	 * timeNote = "Tuần"; break; case 3: time = "thang"; timeNote = "Tháng";
	 * break; case 4: time = "quy"; timeNote = "Quý"; break; default: break; }
	 * // String defaultConfig = //
	 * "{\"chartConfig\":{\"charts\":[],\"legend\":true,\"xAxisLabel\":\"\",\"maxLength\":12,\"legendOrientation\":\"bottom\"},\"name\":\"\",\"id\":\"\",\"providerConfig\":{\"configs\":{\"type\":\"RDBMSBatchDataProvider\",\"config\":{\"purgingInterval\":60,\"publishingInterval\":1,\"publishingLimit\":30,\"purgingLimit\":30,\"isPurgingEnable\":false,\"queryData\":{\"queryFunction\":\"this.getQuery = function (username){return \\\"\\\" ;}\",\"query\":\"\",\"customWidgetInputs\":[],\"incrementalColumn\":\"\",\"timeColumns\":\"\",\"systemWidgetInputs\":[{\"defaultValue\":\"admin\",\"name\":\"username\"}]},\"datasourceName\":\"WSO2_DASHBOARD_DB2\",\"incrementalColumn\":\"\",\"timeColumns\":\"\",\"tableName\":\"\"}}},\"version\":\"1.0.0\",\"pubsub\":{\"types\":[]}}"
	 * ; String defaultConfig =
	 * "{\"name\":\"\",\"id\":\"\",\"chartConfig\":{\"x\":\"\",\"charts\":[],\"legend\":true,\"maxLength\":12,\"yAxisLabel\":\"\"},\"providerConfig\":{\"configs\":{\"type\":\"RDBMSBatchDataProvider\",\"config\":{\"datasourceName\":\"WSO2_DASHBOARD_DB2\",\"queryData\":{\"queryFunction\":\"\",\"customWidgetInputs\":[],\"systemWidgetInputs\":[{\"name\":\"username\",\"defaultValue\":\"admin\"}],\"query\":\"\"},\"tableName\":\"\",\"incrementalColumn\":\"\",\"timeColumns\":\"\",\"publishingInterval\":1,\"purgingInterval\":60,\"publishingLimit\":30,\"purgingLimit\":30,\"isPurgingEnable\":true}}},\"version\":\"1.0.0\",\"pubsub\":{\"types\":[]}}"
	 * ; JSONObject configParser = new JSONObject(defaultConfig); // tungvtd
	 * modify name from widgetId to viewTableName configParser.put("name",
	 * viewTableName); configParser.put("id", widgetId);
	 * 
	 * JSONObject chartConfig = configParser .getJSONObject("chartConfig");
	 * 
	 * // chartConfig.put("x", "");
	 * 
	 * chartConfig.put("xAxisLabel", timeNote); chartConfig.put("yAxisLabel",
	 * yAxisLabel); JSONArray chartsObj = chartConfig.getJSONArray("charts"); if
	 * (type == 4) { JSONObject objConfigPie = new JSONObject();
	 * objConfigPie.put("x", metaData.get(1)); objConfigPie.put("type",
	 * typeWidget); objConfigPie.put("color", metaData.get(0));
	 * objConfigPie.put("mode", "pie"); chartsObj.put(objConfigPie); } else {
	 * chartConfig.put("x", time); for (int i = 0; i < metaData.size(); i++) {
	 * JSONObject obj = new JSONObject(); obj.put("type", typeWidget);
	 * obj.put("y", metaData.get(i)); // obj.put("color", metaData.get(i));
	 * chartsObj.put(obj); } }
	 * 
	 * JSONObject providerConfig = configParser
	 * .getJSONObject("providerConfig"); JSONObject config =
	 * providerConfig.getJSONObject("configs"); JSONObject configQuery =
	 * config.getJSONObject("config"); configQuery.put("incrementalColumn",
	 * time); configQuery.put("timeColumns", time);
	 * 
	 * configQuery.put("tableName", viewTableName); // tungvtd add dbsource
	 * configQuery.put("datasourceName", dbSource);
	 * 
	 * JSONObject queryData = configQuery.getJSONObject("queryData");
	 * 
	 * queryData.put("query", queryDataConfig);
	 * queryData.put("incrementalColumn", time); queryData.put("timeColumns",
	 * time);
	 * 
	 * String queryFunction = "this.getQuery = function (username){return \"" +
	 * queryDataConfig + "\" ;}"; queryData.put("queryFunction", queryFunction);
	 * System.out.println(configParser); return configParser.toString();
	 * 
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return "-1"; } } }
	 * 
	 * public int addDashboard_Resource(String url, String userId, String
	 * widgetId, int sttcId) { String configString =
	 * createDashboardContent(widgetId);
	 * 
	 * Dashboard_Resource dashboard = new Dashboard_Resource();
	 * dashboard.setURL(url); dashboard.setOWNER(userId);
	 * dashboard.setCONTENT(configString); dashboard.setDESCRIPTION("");
	 * dashboard.setLANDING_PAGE("home"); dashboard.setNAME(widgetId);
	 * dashboard.setPARENT_ID(0);
	 * 
	 * MySqlConnection con = new MySqlConnection(); if
	 * (con.insertInWso2(dashboard) != ErrorCode.OK.code) return
	 * ErrorCode.Not_OK.code;
	 * 
	 * Statistical_Dashboard sttcdb = new Statistical_Dashboard();
	 * sttcdb.setOWNER(userId); sttcdb.setURL(url);
	 * sttcdb.setStatisticalId(sttcId);
	 * 
	 * return con.insert(sttcdb); }
	 * 
	 * public int addEmptyDashboard_Resource(String name, String url, String
	 * description, String userId, int sttcId) { Dashboard_Resource dashboard =
	 * new Dashboard_Resource(); dashboard.setURL(url);
	 * dashboard.setOWNER(userId); dashboard
	 * .setCONTENT("[{\"id\":\"home\",\"name\":\"Home\",\"content\":[]}]");
	 * dashboard.setDESCRIPTION(description); // TODO: setLANDING_PAGE from
	 * param dashboard.setLANDING_PAGE("home"); dashboard.setNAME(name);
	 * dashboard.setPARENT_ID(0);
	 * 
	 * MySqlConnection con = new MySqlConnection(); if
	 * (con.insertInWso2(dashboard) != ErrorCode.OK.code) return
	 * ErrorCode.Not_OK.code;
	 * 
	 * Statistical_Dashboard sttcdb = new Statistical_Dashboard();
	 * sttcdb.setOWNER(userId); sttcdb.setURL(url);
	 * sttcdb.setStatisticalId(sttcId);
	 * 
	 * return con.insert(sttcdb); }
	 * 
	 * private String createDashboardContent(String widgetId) { if
	 * (widgetId.isEmpty()) { return "0"; } else {
	 * 
	 * String contentConfig =
	 * "[{\"id\":\"home\",\"name\":\"Home\",\"content\":[{\"type\":\"column\",\"isClosable\":true,\"title\":\"\",\"content\":[{\"type\":\"row\",\"isClosable\":true,\"title\":\"\",\"height\":100.0,\"content\":[{\"type\":\"stack\",\"isClosable\":true,\"title\":\"\",\"height\":100.0,\"width\":100.0,\"content\":[{\"title\":\"[do-phu-cap-quang-theo-dan-so]\",\"type\":\"component\",\"component\":\"UniversalWidget\",\"props\":{\"id\":\"f158e2e3-093a-4171-87d8-09880fbf032c\",\"configs\":{\"pubsub\":{\"types\":[]},\"isGenerated\":true,\"options\":{}},\"widgetID\":\"do-phu-cap-quang-theo-dan-so\"},\"isClosable\":false,\"header\":{\"show\":true},\"componentName\":\"lm-react-component\"}]}]}]}]}]"
	 * ; System.out.println("Data config: " + contentConfig); try { JSONArray
	 * configParser = new JSONArray(contentConfig); JSONObject configParserObj =
	 * configParser.getJSONObject(0); JSONArray configContentArray =
	 * configParserObj .getJSONArray("content"); JSONObject configContentObj =
	 * configContentArray .getJSONObject(0);
	 * 
	 * JSONArray configListWidget = configContentObj .getJSONArray("content");
	 * 
	 * JSONObject objectConfig = configListWidget.getJSONObject(0);
	 * 
	 * JSONArray configListWidget2 = objectConfig .getJSONArray("content");
	 * JSONObject configListWidget3 = configListWidget2 .getJSONObject(0);
	 * 
	 * JSONArray configListWidget4 = configListWidget3 .getJSONArray("content");
	 * JSONObject configListWidget5 = configListWidget4 .getJSONObject(0);
	 * 
	 * configListWidget5.put("title", "[" + widgetId + "]"); JSONObject propsObj
	 * = configListWidget5.getJSONObject("props"); UUID uuid =
	 * UUID.randomUUID(); String randomUUIDString = uuid.toString();
	 * propsObj.put("id", uuid); propsObj.put("widgetID", widgetId);
	 * 
	 * System.out.println("Random UUID String = " + randomUUIDString);
	 * System.out.println("Data After= " + configParser.toString()); return
	 * configParser.toString(); } catch (JSONException e) { // TODO
	 * Auto-generated catch block System.out.println("Exception r");
	 * e.printStackTrace(); return "-1"; }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * public RetValData updateDashboardResource(int sttcId, String userId) {
	 * MySqlConnection conn = new MySqlConnection(); List<String> listWidgetId =
	 * conn.getWidgetIdBySttc(sttcId); String urlDashboardSoutce =
	 * conn.getDashboardIdBySttc(sttcId);
	 * 
	 * if (listWidgetId.size() < 1 || urlDashboardSoutce == "") { return new
	 * RetValData(ErrorCode.Object_Not_Existed.code, "",
	 * "listWidgetId or urlDashboardSoutce empty"); }
	 * 
	 * String contentDashboardSoutce = updateDashboardContent(listWidgetId);
	 * 
	 * Dashboard_Resource dbresource = new Dashboard_Resource();
	 * dbresource.setURL(urlDashboardSoutce);
	 * dbresource.setNAME(urlDashboardSoutce); dbresource.setOWNER(userId);
	 * dbresource.setPARENT_ID(0); dbresource.setLANDING_PAGE("");
	 * dbresource.setDESCRIPTION("");
	 * dbresource.setCONTENT(contentDashboardSoutce);
	 * 
	 * Dashboard_Resource tmp = new Dashboard_Resource();
	 * tmp.setOWNER(dbresource.getOWNER()); tmp.setURL(dbresource.getURL());
	 * 
	 * tmp = conn.getInWso2(Dashboard_Resource.class, tmp); if (tmp.getCONTENT()
	 * == null || tmp.getCONTENT().length() == 0) return new
	 * RetValData(ErrorCode.Object_Not_Existed.code, "",
	 * "Dashboard_Resource.content empty");
	 * 
	 * return new RetValData(conn.updateInWso2(Dashboard_Resource.class,
	 * dbresource)); }
	 * 
	 * // vietpdb wrote // private String updateDashboardContent(List<String>
	 * objWidget) { // String contentDefault = //
	 * "[{\"name\":\"Home\",\"id\":\"home\",\"content\":[{\"type\":\"column\",\"isClosable\":true,\"title\":\"\",\"content\":[]}]}]"
	 * ; // String configDefaults = //
	 * "{\"type\":\"stack\",\"isClosable\":true,\"title\":\"\",\"height\":50.0,\"content\":[{\"title\":\"\",\"type\":\"component\",\"component\":\"UniversalWidget\",\"props\":{\"id\":\"\",\"configs\":{\"pubsub\":{\"types\":[]},\"isGenerated\":true,\"options\":{}},\"widgetID\":\"\"},\"isClosable\":false,\"header\":{\"show\":true},\"componentName\":\"lm-react-component\"}]}"
	 * ; // // JSONArray arrContent = null; // JSONObject objConfig = null,
	 * objContent = null; // try { // arrContent = new
	 * JSONArray(contentDefault); // } catch (JSONException e1) { // // TODO
	 * Auto-generated catch block // e1.printStackTrace(); // // } // if
	 * (objWidget.size() >1) { // try { // float heightValue =
	 * 100/objWidget.size(); // objConfig = new JSONObject(configDefaults); //
	 * objContent = arrContent.getJSONObject(0); // JSONArray arrContentObj =
	 * objContent.getJSONArray("content"); // JSONObject objContentObj =
	 * arrContentObj.getJSONObject(0); // JSONArray arrContentObj2 =
	 * objContentObj.getJSONArray("content"); // // for(int i=0; i <
	 * objWidget.size(); i++) { // // objConfig.put("height", heightValue); //
	 * JSONArray arr = objConfig.getJSONArray("content"); // JSONObject obj =
	 * arr.getJSONObject(0); // obj.put("title", objWidget.get(i)); //
	 * JSONObject objProps = obj.getJSONObject("props"); // UUID uuid =
	 * UUID.randomUUID(); // String randomUUIDString = uuid.toString(); //
	 * objProps.put("id", randomUUIDString); // objProps.put("widgetID",
	 * objWidget.get(i)); // arrContentObj2.put(objConfig); // } // // } catch
	 * (JSONException e) { // // TODO Auto-generated catch block //
	 * e.printStackTrace(); // } // // }else { // return ""; // } //
	 * System.out.println("update Dashboards: "+ arrContent.toString()); //
	 * return arrContent.toString(); // }
	 * 
	 * private String updateDashboardContent(List<String> objWidget) { String
	 * contentDefault =
	 * "[{\"name\":\"Home\",\"id\":\"home\",\"content\":[{\"type\":\"column\",\"isClosable\":true,\"title\":\"\",\"content\":[]}]}]"
	 * ; String configDefaults =
	 * "{\"type\":\"stack\",\"isClosable\":true,\"title\":\"\",\"height\":50.0,\"content\":[{\"title\":\"\",\"type\":\"component\",\"component\":\"UniversalWidget\",\"props\":{\"id\":\"\",\"configs\":{\"pubsub\":{\"types\":[]},\"isGenerated\":true,\"options\":{}},\"widgetID\":\"\"},\"isClosable\":false,\"header\":{\"show\":true},\"componentName\":\"lm-react-component\"}]}"
	 * ;
	 * 
	 * if (objWidget.size() > 1) { float heightValue = 100 / objWidget.size();
	 * try { JSONArray arrContent = new JSONArray(contentDefault); JSONObject
	 * objContent = arrContent.getJSONObject(0); JSONArray arrContentObj =
	 * objContent.getJSONArray("content"); JSONObject objContentObj =
	 * arrContentObj.getJSONObject(0); JSONArray arrContentObj2 = objContentObj
	 * .getJSONArray("content");
	 * 
	 * for (int i = 0; i < objWidget.size(); i++) { JSONObject objConfig = new
	 * JSONObject(configDefaults);
	 * 
	 * objConfig.put("height", heightValue); JSONArray arr =
	 * objConfig.getJSONArray("content"); JSONObject obj = arr.getJSONObject(0);
	 * obj.put("title", objWidget.get(i)); JSONObject objProps =
	 * obj.getJSONObject("props"); UUID uuid = UUID.randomUUID(); String
	 * randomUUIDString = uuid.toString(); objProps.put("id", randomUUIDString);
	 * objProps.put("widgetID", objWidget.get(i));
	 * arrContentObj2.put(objConfig); } System.out.println("update Dashboards: "
	 * + arrContent.toString()); return arrContent.toString();
	 * 
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return ""; }
	 * 
	 * } else { return ""; } }
	 */
	public List<ChartPresentObject> convertJsonArrayToChartPresentObjectList(
			JSONArray presentArray) {
		List<ChartPresentObject> retVal = new ArrayList<ChartPresentObject>();

		// jsonObject = "["+
		// "{"+
		// "\"id\": 23," +
		// "\"name\": \"Doanh thu 3G_Viettel\"," +
		// "\"type\": ["+
		// "{"+
		// "\"type\": 1,"+
		// "\"value\": 1"+
		// "},"+
		// "{"+
		// "\"type\": 2,"+
		// "\"value\": 201"+
		// "}"+
		// "]"+
		// "},"+
		// "{"+
		// "\"id\": 23,"+
		// "\"name\": \"Doanh thu 3G_Vinaphone\","+
		// "\"type\": ["+
		// "{"+
		// "\"type\": 1,"+
		// "\"value\": 1"+
		// "},"+
		// "{"+
		// "\"type\": 2,"+
		// "\"value\": 201"+
		// "}"+
		// "]"+
		// "},"+
		// "{"+
		// "\"id\": 23,"+
		// "\"name\": \"Doanh thu 3G_Mobifone\","+
		// "\"type\": ["+
		// "{"+
		// "\"type\": 1,"+
		// "\"value\": 1"+
		// "},"+
		// "{"+
		// "\"type\": 2,"+
		// "\"value\": 201"+
		// "}"+
		// "]"+
		// "}"+
		// "]";
		try {

			System.out.println("presentArray size :" + presentArray.length());
			for (int i = 0; i < presentArray.length(); i++) {
				ChartPresentObject chartPresentObject = new ChartPresentObject();

				String temp = presentArray.getString(i);
				System.out.println("temp." + i + ":" + temp);
				JSONObject jsonChartPresentObject = new JSONObject(temp);
				System.out.println("id = " + jsonChartPresentObject.get("id"));
				System.out.println("name = "
						+ jsonChartPresentObject.get("name"));
				JSONArray jsonChartPresentObjectTypeArray = jsonChartPresentObject
						.getJSONArray("type");
				System.out.println("jsonChartPresentObjectType size: "
						+ jsonChartPresentObjectTypeArray.length());

				for (int j = 0; j < jsonChartPresentObjectTypeArray.length(); j++) {
					ChartPresentObjectType chartPresentObjectType = new ChartPresentObjectType();

					String tempType = jsonChartPresentObjectTypeArray
							.getString(j);
					System.out.println("tempType." + i + ":" + tempType);
					JSONObject jsonChartPresentObjectType = new JSONObject(temp);
					System.out.println("type = "
							+ jsonChartPresentObjectType.get("type"));
					System.out.println("value = "
							+ jsonChartPresentObjectType.get("value"));

					chartPresentObjectType.setType(Integer
							.parseInt(jsonChartPresentObjectType
									.getString("type")));
					chartPresentObjectType.setValue(Integer
							.parseInt(jsonChartPresentObjectType
									.getString("value")));
					chartPresentObject.appendType(chartPresentObjectType);
				}

				// set properties to object
				chartPresentObject.setId(Integer
						.parseInt(jsonChartPresentObject.getString("id")));
				chartPresentObject.setName(jsonChartPresentObject
						.getString("name"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retVal;
	}

	private static int checkDBViewMatched(int checkType, // 2: require filter
															// all (filter size
															// = table,formula
															// view size)
															// 1: not require
															// filter all (allow
															// filter size =
															// table,formula
															// view size)
			int dbObjectType, // 2: table
								// 1: formula
			JSONArray dbViewArr, JSONArray filterViewArr) {
		// System.out.println(tableViewArr.toString());
		// System.out.println(filterViewArr.toString());
		try {
			if (dbViewArr.length() < filterViewArr.length()) {
				return -1;
			} else if (dbViewArr.length() > filterViewArr.length()
					&& checkType == 2) {
				// TODO: quy hoach thanh ma loi
				return -1;
			}
			List<Integer> matches = new ArrayList<Integer>();
			for (int i = 0; i < filterViewArr.length(); i++) {
				// boolean isHaveMathched = false;

				JSONObject filterView = filterViewArr.getJSONObject(i);
				for (int j = 0; j < dbViewArr.length(); j++) {
					JSONObject tableView = dbViewArr.getJSONObject(j);

					if (dbObjectType == 1) {
						if (tableView.getInt("viewGroupId") == filterView
								.getInt("viewGroupId")) {
							// System.out.println(tableView.toString());
							// System.out.println(tableView.getInt("Level"));
							if (tableView.getInt("Level") == filterView
									.getJSONObject("data").getInt("Level")) {
								matches.add(0);
							} else if (tableView.getInt("Level") < filterView
									.getJSONObject("data").getInt("Level")) {
								// matches.add(-1);
								return -1;
							} else {
								matches.add(1);
							}
							break;
						}
					} else {
						if (tableView.getInt("viewGroupId") == filterView
								.getInt("viewGroupId")) {
							// System.out.println(tableView.toString());
							// System.out.println(tableView.getInt("Level"));
							if (tableView.getJSONObject("data").getInt("Level") == filterView
									.getJSONObject("data").getInt("Level")) {
								matches.add(0);
							} else if (tableView.getJSONObject("data").getInt(
									"Level") < filterView.getJSONObject("data")
									.getInt("Level")) {
								// matches.add(-1);
								return -1;
							} else {
								matches.add(1);
							}
							break;
						}
					}
				}
			}
			for (Integer match : matches) {
				if (match == 1) {
					return match;
				}
			}

			return 0;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	// fix to hide column not year
	// private static int checkTableViewMatched(JSONArray tableViewArr,
	// JSONArray filterViewArr){
	// // System.out.println(tableViewArr.toString());
	// // System.out.println(filterViewArr.toString());
	// try {
	// if (tableViewArr.length() < filterViewArr.length()){
	// return -1;
	// }
	// List<Integer> matches = new ArrayList<Integer>();
	// for (int i = 0; i <filterViewArr.length(); i ++){
	// //boolean isHaveMathched = false;
	//
	// JSONObject filterView = filterViewArr.getJSONObject(i);
	// for (int j = 0; j <tableViewArr.length(); j ++){
	// JSONObject tableView = tableViewArr.getJSONObject(j);
	//
	// if (tableView.getInt("viewGroupId") == filterView.getInt("viewGroupId")){
	// // System.out.println(tableView.toString());
	// // System.out.println(tableView.getInt("Level"));
	// if (tableView.getInt("Level") ==
	// filterView.getJSONObject("data").getInt("Level")){
	// matches.add(0);
	// } else if(tableView.getInt("Level") <
	// filterView.getJSONObject("data").getInt("Level")){
	// //matches.add(-1);
	// return -1;
	// } else {
	// if (tableView.getInt("viewGroupId") == 2){
	// if (filterView.getJSONObject("data").getInt("Level") == 1){//year
	// matches.add(1);
	// } else {
	// return -1;
	// }
	// } else {
	// matches.add(1);
	// }
	// }
	// break;
	// }
	// }
	// }
	// for (Integer match:matches){
	// if (match == 1){
	// return match;
	// }
	// }
	//
	// return 0;
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return -1;
	// }
	// }

	private static int checkFormulaViewMatch(JSONArray tableViewArr,
			JSONArray filterViewArr) {
		try {
			if (tableViewArr.length() < filterViewArr.length()) {
				return -1;
			}
			List<Integer> matches = new ArrayList<Integer>();
			for (int i = 0; i < filterViewArr.length(); i++) {
				// boolean isHaveMathched = false;

				JSONObject filterView = filterViewArr.getJSONObject(i);
				for (int j = 0; j < tableViewArr.length(); j++) {
					JSONObject tableView = tableViewArr.getJSONObject(j);

					if (tableView.getInt("viewGroupId") == filterView
							.getInt("viewGroupId")) {
						// System.out.println(tableView.toString());
						// System.out.println(tableView.getInt("Level"));
						if (tableView.getInt("Level") == filterView
								.getJSONObject("data").getInt("Level")) {
							matches.add(0);
						} else if (tableView.getInt("Level") < filterView
								.getJSONObject("data").getInt("Level")) {
							// matches.add(-1);
							return -1;
						} else {
							matches.add(1);
						}
						break;
					}
				}
			}
			for (Integer match : matches) {
				if (match == 1) {
					return match;
				}
			}

			return 0;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	private static RetValData checkFormulaValidated(JSONObject view,
			JSONObject data) {
		try {
			if (!Utility.isJsonKeyExistedAndNotNull(view, "view")) {

				return new RetValData(ErrorCode.Invalid_Input.code, "",
						"view null");
			}
			if (data.has("type")) {
				int type = data.getInt("type");
				if (type == 1) {
					BIChartPureData pureData = new BIChartPureData();
					return pureData.setData(data);
				} else if (type == 3) {
					BIChartMixData mixData = new BIChartMixData();
					return mixData.setData(data);
				} else if (type == 4) {
					BICondition condition = new BICondition();
					return condition.setData(data);
				}
				return new RetValData(ErrorCode.Invalid_Input.code, "",
						"type invalid: " + type);
			}
			return new RetValData(ErrorCode.Invalid_Input.code, "", "type null");
		} catch (JSONException e) {
			e.printStackTrace();
			return new RetValData(ErrorCode.Invalid_Input.code, e.getMessage(),
					"json exception");
		}
	}
	public static RetValData getDataFromSQLResultColumn(String firstKey, String secondKey, String columnData){
		try {
			JSONArray array = new JSONArray(columnData);
			JSONObject object = array.getJSONObject(array.length() - 1);
			
			if (object.has(secondKey)){
				return new RetValData(object.getString(secondKey));
			} else {
				return new RetValData(ErrorCode.Invalid_Input.code, "", "secondKey not exist");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return new RetValData(ErrorCode.Invalid_Input.code, e.getMessage(), "columnData is not json array");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// MySqlConnection mysql = new MySqlConnection();
		// BIViewGroup viewGR = mysql.get(BIViewGroup.class, 1);
		// System.out.println(viewGR);

		 String param =
		 "{\"grp\":\"1\",\"filter\":[{\"viewGroupId\":1,\"name\":\"Regional\",\"description\":\"\u0110\u1ecba ph\u01b0\u01a1ng\",\"data\":{\"ColumnId\":10,\"ParentCode\":0,\"Description\":\"\",\"NameInDB\":\"madiaban\",\"Level\":1,\"Code\":\"15\",\"Name\":\"Y\u00ean B\u00e1i\"}},{\"viewGroupId\":2,\"name\":\"Time\",\"description\":\"Chu k\u1ef3\",\"data\":{\"ColumnId\":68,\"ParentCode\":2,\"Description\":\"\",\"NameInDB\":\"quarterkey\",\"Level\":1,\"Code\":\"3\",\"Name\":\"Qu\u00fd\"}},{\"viewGroupId\":3,\"name\":\"Business\",\"description\":\"Doanh nghi\u1ec7p\",\"data\":{\"ColumnId\":76,\"ParentCode\":-1,\"Description\":\"\",\"NameInDB\":\"organize\",\"Level\":1,\"Code\":\"1\",\"Name\":\"C\u1ea3 n\u01b0\u1edbc\"}}]}";
//		 String param = "{\"grp\":\"1\", \"filter\":[]}";
		 String abc = Controller.getDescriptionOfBIColumn(param);
		 System.out.println(abc);

		// get filter
		// Controller abcd = new Controller();
		// RetValData ret = abcd.getFilter();
		// System.out.println(ret.getData());

		// create forumula
//		Controller ctrl = new Controller();
//		String sql = "select t0.yearkey as yearkey, t0.sothuebaochuyenmanggiunguyenso_sothuebaochuyendi as 'S? thuê bao chuy?n m?ng gi? s? chuy?n ?i', t1.sothuebaodangkychuyenmang as 'S? thuê bao ??ng k? chuy?n m?ng gi? s?', t2.sothuebaochuyenmanggiunguyenso_sothuebaochuyenden as 'S? thuê bao chuy?n m?ng gi? s? chuy?n ??n' from (select yearkey, round(sum(sothuebaochuyenmanggiunguyenso_sothuebaochuyendi), 2) as sothuebaochuyenmanggiunguyenso_sothuebaochuyendi  from bcchuyenmanggiuso  where diaban='Hà N?i' and yearkey='N?m'  group by yearkey) as t0 left join (select yearkey, round(sum(sothuebaodangkychuyenmang), 2) as sothuebaodangkychuyenmang  from bcchuyenmanggiuso  where diaban='Hà N?i' and yearkey='N?m'  group by yearkey) as t1 on t0.yearkey = t1.yearkey left join (select yearkey, round(sum(sothuebaochuyenmanggiunguyenso_sothuebaochuyenden), 2) as sothuebaochuyenmanggiunguyenso_sothuebaochuyenden  from bcchuyenmanggiuso  where diaban='Hà N?i' and yearkey='N?m'  group by yearkey) as t2 on t0.yearkey = t2.yearkey  group by yearkey order by yearkey";
//		MySqlConnection conn = new MySqlConnection();
//		FormulaChart chart = conn.get(FormulaChart.class, 10);
//		System.out.println(ctrl.renderChartOfFormula("chartName",
//				chart.getXaxis(),sql));

//		//getDataFromsqlColumn
//		RetValData data = Controller.getDataFromSQLResultColumn("", "test","[{\"monthkey\":201301,\"test\":1.27},{\"monthkey\":201302,\"test\":1.6},{\"monthkey\":201303,\"test\":2.43},{\"monthkey\":201304,\"test\":2.72},{\"monthkey\":201305,\"test\":1.74},{\"monthkey\":201306,\"test\":1.96},{\"monthkey\":201307,\"test\":1.18},{\"monthkey\":201308,\"test\":1.18},{\"monthkey\":201309,\"test\":1},{\"monthkey\":201310,\"test\":1.18},{\"monthkey\":201311,\"test\":1.68},{\"monthkey\":201312,\"test\":0.61},{\"monthkey\":201401,\"test\":0.75},{\"monthkey\":201402,\"test\":2.09},{\"monthkey\":201403,\"test\":0.81},{\"monthkey\":201404,\"test\":1.32},{\"monthkey\":201405,\"test\":1.42},{\"monthkey\":201406,\"test\":1.05},{\"monthkey\":201407,\"test\":2.68},{\"monthkey\":201408,\"test\":0.66},{\"monthkey\":201409,\"test\":3.42},{\"monthkey\":201410,\"test\":1.98},{\"monthkey\":201411,\"test\":1.93},{\"monthkey\":201412,\"test\":2.18},{\"monthkey\":201501,\"test\":1.29},{\"monthkey\":201502,\"test\":3.1},{\"monthkey\":201503,\"test\":2.28},{\"monthkey\":201504,\"test\":0.89},{\"monthkey\":201505,\"test\":1.73},{\"monthkey\":201506,\"test\":1.73},{\"monthkey\":201507,\"test\":1.3},{\"monthkey\":201508,\"test\":3.41},{\"monthkey\":201509,\"test\":0.52},{\"monthkey\":201510,\"test\":0.77},{\"monthkey\":201511,\"test\":1.14},{\"monthkey\":201512,\"test\":3.65},{\"monthkey\":201601,\"test\":2.72},{\"monthkey\":201602,\"test\":2.08},{\"monthkey\":201603,\"test\":2.06},{\"monthkey\":201604,\"test\":2.5},{\"monthkey\":201605,\"test\":2.4},{\"monthkey\":201606,\"test\":0.36},{\"monthkey\":201607,\"test\":3.26},{\"monthkey\":201608,\"test\":1.43},{\"monthkey\":201609,\"test\":1.22},{\"monthkey\":201610,\"test\":1.28},{\"monthkey\":201611,\"test\":2.83},{\"monthkey\":201612,\"test\":1.15},{\"monthkey\":201701,\"test\":0.99},{\"monthkey\":201702,\"test\":3.31},{\"monthkey\":201703,\"test\":2.2},{\"monthkey\":201704,\"test\":1.29},{\"monthkey\":201705,\"test\":1.02},{\"monthkey\":201706,\"test\":0.6},{\"monthkey\":201707,\"test\":0.81},{\"monthkey\":201708,\"test\":2.46},{\"monthkey\":201709,\"test\":0.95},{\"monthkey\":201710,\"test\":2.76},{\"monthkey\":201711,\"test\":0.91},{\"monthkey\":201712,\"test\":0.68},{\"monthkey\":201801,\"test\":2.77},{\"monthkey\":201802,\"test\":1.5},{\"monthkey\":201803,\"test\":2.83},{\"monthkey\":201804,\"test\":2.61},{\"monthkey\":201805,\"test\":1.76},{\"monthkey\":201806,\"test\":0.57},{\"monthkey\":201807,\"test\":2.11},{\"monthkey\":201808,\"test\":1.43},{\"monthkey\":201809,\"test\":0.66},{\"monthkey\":201810,\"test\":2.99},{\"monthkey\":201811,\"test\":1.08},{\"monthkey\":201812,\"test\":1.24}]");
//		System.out.println(data);
//		System.out.println(data.getData());
//		BIMICService servic = new BIMICService();
//		RetValData ret = servic.createFormula("{\"type\":2,\"view\":{\"xAxis\":\"yearkey\",\"view\":{\"grp\":\"11\",\"filter\":[{\"viewGroupId\":1,\"name\":\"Regional\",\"description\":\"\u0110\u1ecba ph\u01b0\u01a1ng\",\"data\":{\"ColumnId\":0,\"ParentCode\":-1,\"Description\":\"\",\"NameInDB\":\"diaban\",\"Level\":1,\"Code\":\"00\",\"Name\":\"Vi\u1ec7t Nam\"}},{\"viewGroupId\":2,\"name\":\"Time\",\"description\":\"Chu k\u1ef3\",\"data\":{\"ColumnId\":70,\"ParentCode\":-1,\"Description\":\"\",\"NameInDB\":\"yearkey\",\"Level\":1,\"Code\":\"1\",\"Name\":\"N\u0103m\"}},{\"viewGroupId\":3,\"name\":\"Business\",\"description\":\"Doanh nghi\u1ec7p\",\"data\":{\"ColumnId\":71,\"ParentCode\":-1,\"Description\":\"\",\"NameInDB\":\"organizekey\",\"Level\":1,\"Code\":\"1\",\"Name\":\"C\u1ea3 n\u01b0\u1edbc\"}}]}},\"data\":{\"componentList\":[{\"tableName\":\"vt_tsvtd_luong_pho_tan_imt\",\"columnName\":\"photanphanchiachoquocphonganninh_den\",\"dbTimeType\":\"yearkey\",\"type\":1,\"alias\":\"Ph\u1ed5 t\u1ea7n ph\u00e2n chia cho Qu\u1ed1c ph\u00f2ng an ninh (MHz) - \u0110\u1ebfn\",\"function\":\"\",\"conditionList\":[{\"condition\":\"=\",\"conditionValue\":\"Vi\u1ec7t Nam\",\"columnValueType\":1,\"columnName\":\"diaban\"},{\"condition\":\"=\",\"conditionValue\":\"C\u1ea3 n\u01b0\u1edbc\",\"columnValueType\":1,\"columnName\":\"organizekey\"},{\"columnName\":\"photanphanchiachoquocphonganninh_den\",\"columnValueType\":2,\"condition\":\"<\",\"conditionValue\":\"5000\"}]},{\"type\":2,\"operator\":\"*\"},{\"tableName\":\"vt_tsvtd_luong_pho_tan_imt\",\"columnName\":\"photanphanchiachoquocphonganninh_den\",\"dbTimeType\":\"yearkey\",\"type\":1,\"alias\":\"Ph\u1ed5 t\u1ea7n ph\u00e2n chia cho Qu\u1ed1c ph\u00f2ng an ninh (MHz) - \u0110\u1ebfn\",\"function\":\"\",\"conditionList\":[{\"condition\":\"=\",\"conditionValue\":\"Vi\u1ec7t Nam\",\"columnValueType\":1,\"columnName\":\"diaban\"},{\"condition\":\"=\",\"conditionValue\":\"C\u1ea3 n\u01b0\u1edbc\",\"columnValueType\":1,\"columnName\":\"organizekey\"},{\"columnName\":\"photanphanchiachoquocphonganninh_den\",\"columnValueType\":2,\"condition\":\">\",\"conditionValue\":\"100\"}]}],\"alias\":\"testing\",\"type\":3}}");
//		System.out.println(ret);
//		System.out.println(ret.getData());
	}
}
