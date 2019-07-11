package com.bkav.aic.service;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bkav.aic.bmm.object.Mission;
import com.bkav.aic.bmm.object.tbl_User;
import com.bkav.aic.control.BISQLController;
import com.bkav.aic.control.Controller;
import com.bkav.aic.dbconnection.MySqlConnection;
import com.bkav.aic.dbconnection.SQLServerConnection;
import com.bkav.aic.define.Define;
import com.bkav.aic.define.ErrorCode;
import com.bkav.aic.define.DashBoardData;
import com.bkav.aic.define.RetValData;
import com.bkav.aic.object.AdvancedFormula;
import com.bkav.aic.object.BIColumn;
import com.bkav.aic.object.BIFormula;
import com.bkav.aic.object.BITable;
import com.bkav.aic.object.ChartForm;
import com.bkav.aic.object.ChartObject;
import com.bkav.aic.object.ChartPresentObject;
import com.bkav.aic.object.DashboardConfig;
import com.bkav.aic.object.Dashboard_Resource;
import com.bkav.aic.object.Field;
import com.bkav.aic.object.Formula;
import com.bkav.aic.object.FormulaChart;
import com.bkav.aic.object.Group;
import com.bkav.aic.object.Statistical;
import com.bkav.aic.object.TimeTool;
import com.bkav.aic.object.User;
import com.bkav.aic.object.Widget_Resource;
import com.bkav.aic.object.sql.BIColumnInChart;
import com.bkav.aic.utils.Base64Utils;
import com.bkav.aic.utils.Utility;
import com.ctc.wstx.io.EBCDICCodec;
import com.sun.xml.internal.ws.api.ha.StickyFeature;

public class BIMICService {
	@SuppressWarnings("unused")
	private final int _____other_____ = 0;

	protected boolean iWriteLog = true;

	public RetValData getListDataTableOfChart(int sttcId, int yearkey) {
		MySqlConnection con = new MySqlConnection();
		List<String> list = con.getQueryBySttc(sttcId);
		JSONArray arr = new JSONArray();
		for (String a : list) {
			JSONObject obj = new JSONObject();
			try {
				String dataTable = con
						.getColumnResult(a + "and nam=" + yearkey);
				obj.put("dataTable", dataTable);
				arr.put(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new RetValData(ErrorCode.OK.code, arr.toString(), "");
	}

	public RetValData getDataTableFromSQL(String userId, int sttcId) {
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);

			MySqlConnection con = new MySqlConnection();
			Formula formula = con.getFormulaById(sttcId);
			if (formula == null)
				return new RetValData(ErrorCode.Object_Not_Existed.code, "",
						"null");
			String dataTable = con.getColumnResult(formula.getSql());

			return new RetValData(ErrorCode.OK.code, dataTable, "");
		}
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	public static RetValData getDocumentOfUser(String userId, int fieldId) {
		// if (userId != "" || userId != null) {
		MySqlConnection conn = new MySqlConnection();
		User user = conn.get(User.class, userId);
		if (user == null) {
			return new RetValData(ErrorCode.User_Not_Existed.code);
		}

		if (user.getLoginState() != 1)
			return new RetValData(ErrorCode.User_Not_Login.code);

		Controller ctrl = new Controller();
		String doc = ctrl.getDocumentByUser(user, fieldId);
		System.out.println("Call getDocumentOfUser");
		if (doc != "")
			return new RetValData(ErrorCode.OK.code, doc, "");
		return new RetValData(ErrorCode.Not_OK.code, doc, "");
		// }
		// System.out.println("getDocumentOfUser - userId null");
		// return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	/*
	 * public RetValData sendSMS(String data, String phoneNumber) { Controller
	 * ctrl = new Controller(); int result = ctrl.sendSMS(data, phoneNumber); if
	 * (result == 0) return new RetValData(ErrorCode.OK.code, "", ""); else
	 * return new RetValData(ErrorCode.Not_OK.code, "", "Exception"); }
	 */

	@SuppressWarnings("unused")
	private final int _____bmm_____ = 0;

	public static RetValData getDepartment(String userId) {
		if (userId != "" || userId != null) {
			SQLServerConnection conSQL = new SQLServerConnection();
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}
			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);

			List<tbl_User> list = conSQL.getUser();

			JSONArray arr = new JSONArray();
			for (tbl_User it : list) {
				if (it.getUser() == userId) {
					continue;
				}
				JSONObject obj = new JSONObject();
				try {
					obj.put("UserId", it.getUser());
					obj.put("UserName", it.getUserName());
					obj.put("RoleId", it.getRoleId());
					obj.put("ObjectGuid", it.getPbjectGuid());
					obj.put("ReceiveEmail", it.getReceiveEmail());
					obj.put("PhoneNumber", it.getPhoneNumber());
					arr.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return new RetValData(ErrorCode.OK.code, arr.toString(), "");

		}
		System.out.println("listGrp - userId null");
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");

	}

	public static RetValData getListUserDept(String userId) {
		if (userId != "" || userId != null) {
			SQLServerConnection conSQL = new SQLServerConnection();
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}
			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			JSONArray arr = new JSONArray();
			List<tbl_User> list = conSQL.getUser();
			for (tbl_User item : list) {
				if (userId.equals(item.getUser())) {
					continue;
				} else {
					JSONObject obj = new JSONObject();

					try {
						obj.put("userExecute", item.getUser());
						obj.put("unitname", item.getUserName());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					arr.put(obj);
				}

			}
			return new RetValData(ErrorCode.OK.code, arr.toString(), "");

		}
		System.out.println("getListUserDept - userId= " + userId);
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	/*
	 * public static RetValData addMisstionProgressOfSttc(String userId, String
	 * objData) { if (userId != "" || userId != null) { SQLServerConnection
	 * conSQL = new SQLServerConnection(); MySqlConnection conn = new
	 * MySqlConnection(); User user = conn.get(User.class, userId); if (user ==
	 * null) { return new RetValData(ErrorCode.User_Not_Existed.code); } if
	 * (user.getLoginState() != 1) return new
	 * RetValData(ErrorCode.User_Not_Login.code);
	 * 
	 * } System.out.println("listGrp - userId null"); return new
	 * RetValData(ErrorCode.Not_OK.code, "", "userId null"); }
	 */
	public static RetValData addMisstionOfSttc(String userId, String objData) {
		if (userId != "" || userId != null) {
			SQLServerConnection conSQL = new SQLServerConnection();
			MySqlConnection conn = new MySqlConnection();
			Controller ctrl = new Controller();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}
			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			try {
				System.out.println("userId : " + userId);
				System.out.println("objData : " + objData);
				JSONObject obj = new JSONObject(objData);
				Statistical tk = conn.get(Statistical.class,
						Integer.valueOf(obj.getString("sttcId")));

				Mission entity = new Mission();
				entity.setMissionStatusId(1);
				entity.setDepartmentExecute("3.3");
				entity.setDepartmentMonitor("3");
				entity.setUserMonitor("dungdt");
				entity.setDocumentDate(new java.sql.Date(System
						.currentTimeMillis()));
				entity.setMissionKeyword("HT");
				entity.setCategoryId(2);
				entity.setObjectGuid(UUID.randomUUID().toString());
				entity.setCreateDate(new java.sql.Date(System
						.currentTimeMillis()));
				entity.setBeginDate(new java.sql.Date(System
						.currentTimeMillis()));
				entity.setEndDate(new java.sql.Date(System.currentTimeMillis()));
				entity.setUpdatedTime(new java.sql.Date(System
						.currentTimeMillis()));
				entity.setPhoneNumber("");
				entity.setDocumentSign(userId);
				entity.setDocumentSigner("");
				if (tk.getName() != "" || tk.getName() != null)
					entity.setDocumentTitle("Chỉ số: " + tk.getName());
				entity.setMissionContent(obj.getString("missionContent"));
				entity.setUserExecute(obj.getString("userExecute"));
				entity.setNote("");
				entity.setSttcId(Integer.valueOf(obj.getString("sttcId")));

				int result = conSQL.insert(entity);
				if (result == 1 || result == 0) {
					ctrl.sendSMS(
							entity.getDocumentTitle() + " \nNội dung yêu cầu: "
									+ entity.getMissionContent()
									+ " \nNgười giao: "
									+ ctrl.getUserNameDept(userId), ctrl
									.getPhonenumberOfUserDept(obj
											.getString("userExecute")));
					return new RetValData(ErrorCode.OK.code, "", "");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new RetValData(ErrorCode.Not_OK.code, "",
						"JSONException objData");
			}

		}
		System.out.println("listGrp - userId null");
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	public static RetValData getMissonOfSttc(String userId, int sttcId) {

		SQLServerConnection conSQL = new SQLServerConnection();
		MySqlConnection conn = new MySqlConnection();
		Controller ctl = new Controller();
		User user = conn.get(User.class, userId);
		if (user == null) {
			return new RetValData(ErrorCode.User_Not_Existed.code);
		}
		if (user.getLoginState() != 1)
			return new RetValData(ErrorCode.User_Not_Login.code);

		JSONObject objMission = new JSONObject();
		JSONArray arr = new JSONArray();
		List<Mission> list = conSQL.getMission();
		if (list.size() < 0) {
			try {
				objMission.put("type", 0);
				objMission.put("listMission", arr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		for (Mission it : list) {
			if (sttcId != it.getSttcId()) {
				continue;
			}
			JSONObject obj = new JSONObject();

			try {
				obj.put("missionId", it.getMissionId());
				obj.put("unitname", conSQL.getUserName(it.getUserExecute()));
				obj.put("time", it.getCreateDate());
				obj.put("status",
						conSQL.getMissionStatus(it.getMissionStatusId()));
				obj.put("content", it.getMissionContent());
				obj.put("missionReport",
						ctl.getMissionReport(it.getMissionId()));

				/*
				 * obj.put("MissionId", it.getMissionId());
				 * obj.put("MissionStatusId", it.getMissionStatusId());
				 * obj.put("ObjectGuid", it.getObjectGuid());
				 * obj.put("UserExecute", it.getUserExecute());
				 * obj.put("DepartmentExecute", it.getDepartmentExecute());
				 * obj.put("UserMonitor", it.getUserMonitor());
				 * obj.put("DepartmentMonitor", it.getDepartmentMonitor());
				 * obj.put("DocumentSign", it.getDocumentSign());
				 * obj.put("DocumentDate", it.getDocumentDate());
				 * obj.put("DocumentField", it.getDocumentField());
				 * obj.put("DocumentExigent", it.getDocumentExigent());
				 * obj.put("DocumentType", it.getDocumentType());
				 * obj.put("DocumentTitle", it.getDocumentTitle());
				 * obj.put("MissionContent", it.getMissionContent());
				 * obj.put("MissionKeyword", it.getMissionKeyword());
				 * obj.put("BeginDate", it.getBeginDate()); obj.put("EndDate",
				 * it.getEndDate()); obj.put("Note", it.getUpdatedTime());
				 * obj.put("CreateDate", it.getCreateDate());
				 * obj.put("DocumentId", it.getDocumentId());
				 * obj.put("DocumentSigner", it.getDocumentSigner());
				 * obj.put("CategoryId", it.getCategoryId());
				 * obj.put("SystemSource", it.getSystemSource());
				 * obj.put("UpdatedTime", it.getUpdatedTime());
				 * obj.put("PhoneNumber", it.getPhoneNumber());
				 * obj.put("SttcId", it.getSttcId());
				 */
				arr.put(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				objMission.put("type", 1);
				objMission.put("listMission", arr);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return new RetValData(ErrorCode.OK.code, objMission.toString(), "");

	}
	@SuppressWarnings("unused")
	private final int _____BI_____ = 0;
	
	public RetValData getBITable(String userId) {
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);

			JSONArray arr = new JSONArray();
			List<BITable> list = conn.getListAll(BITable.class);
			for(BITable biTable : list){
				JSONObject obj = new JSONObject();
				try {
					obj.put("idTable", biTable.getID());
					obj.put("nameTable", biTable.getName());
					arr.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new RetValData(ErrorCode.Not_OK.code, "", "printStackTrace");
				}
				
			}
			return new RetValData(ErrorCode.OK.code, arr.toString(), "");
			
		}
		return new RetValData(ErrorCode.Not_OK.code, "", "");
	}

	@SuppressWarnings("unused")
	private final int _____field_____ = 0;

	public RetValData getFieldOfUser(String userId) {

		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);

			return new RetValData(ErrorCode.OK.code, conn.getJSONFieldsLevel(),
					"u");
		}
		return new RetValData(ErrorCode.Not_OK.code, "", "");
	}

	@SuppressWarnings("unused")
	private final int _____group_____ = 0;

	public RetValData updateGroupOfField(String userId, int fieldId, int grpId,
			int sttcId, String groupName) {
		System.out.println("Call updateGroupOfField");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("fieldId: " + fieldId);
			System.out.println("sttcId: " + sttcId);
			System.out.println("groupName: " + groupName);

		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);

			Group group = conn.get(Group.class, grpId);
			group.setFieldId(fieldId);
			group.setGroupName(groupName);
			group.setSttcId(sttcId);

			int result = conn.update(Group.class, group);
			if (result == 0)
				return new RetValData(ErrorCode.OK.code, "", "");
			else
				return new RetValData(ErrorCode.Not_OK.code,
						String.valueOf(result), "");

		}
		return new RetValData(ErrorCode.Not_OK.code);
	}

	public RetValData removeGroupOfField(String userId, int grpId) {
		System.out.println("Call removeGroupOfField");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("fieldId: " + grpId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);

			if (grpId > 0) {
				int result = conn.delete(Group.class, grpId);
				if (result == 0) {
					return new RetValData(ErrorCode.OK.code, "", "");
				}
				return new RetValData(ErrorCode.Not_OK.code,
						String.valueOf(result), "");
			}
		}
		return new RetValData(ErrorCode.Not_OK.code);
	}

	// public static RetValData getDepartmentOfUser(String userId) {
	// if (userId != "" || userId != null) {
	// MySqlConnection conn = new MySqlConnection();
	// User user = conn.get(User.class, userId);
	// if (user == null) {
	// return new RetValData(ErrorCode.User_Not_Existed.code);
	// }
	//
	// if (user.getLoginState() != 1)
	// return new RetValData(ErrorCode.User_Not_Login.code);
	// List<Department> dpment = conn.getListDepartmentByUserId(userId);
	// JSONArray arrLisrGrp = new JSONArray();
	// for (Department item : dpment) {
	// JSONObject obj = new JSONObject();
	// try {
	// obj.put("id", item.getDepartmentid());
	// obj.put("name", item.getDepartmentname());
	// obj.put("description", item.getDescription());
	// obj.put("userId", item.getUserId());
	// arrLisrGrp.put(obj);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return new RetValData(ErrorCode.OK.code, arrLisrGrp.toString(), "");
	//
	// }
	// System.out.println("listGrp - userId null");
	// return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	// }
	public RetValData getListGroupOfField(String userId, int fieldId) {
		System.out.println("Call getListGroupOfField");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			List<Group> fieldList = conn.getListGroupByUserId(userId, fieldId);
			JSONArray array = new JSONArray();
			for (Group grp : fieldList) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("groupName", grp.getGroupName());
					obj.put("fieldName", grp.getFieldId());
					obj.put("groupId", grp.getGroupId());
					obj.put("fieldId", grp.getFieldId());
					Field field = conn.get(Field.class, grp.getFieldId());
					obj.put("fieldName", field.getName());
					if (grp.getSttcId() > 0) {
						Statistical sttc = conn.get(Statistical.class,
								grp.getSttcId());
						obj.put("sttcName", sttc.getName());
						obj.put("sttcId", grp.getSttcId());
					} else {
						obj.put("sttcName", "");
						obj.put("sttcId", "");
					}
					array.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return new RetValData(ErrorCode.OK.code, array.toString(), "");
		}
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	public RetValData getListFieldByUser(String userId) {
		System.out.println("Call getListFieldByUser");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			List<Field> fieldList = conn.getListFieldByUserId(userId);
			JSONArray arrLisrGrp = new JSONArray();
			for (Field item : fieldList) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("id", item.getId());
					obj.put("name", item.getName());
					obj.put("description", item.getDescription());
					obj.put("userId", item.getUserId());
					arrLisrGrp.put(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return new RetValData(ErrorCode.OK.code, arrLisrGrp.toString(), "");

		}
		System.out.println("listGrp - userId null");
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	// TODO: add security to ws
	public RetValData getGroupDetailByUser(String userId, int fieldId) {
		System.out.println("Call getGroupDetailByUser");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("fieldId: " + fieldId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			Controller ctrl = new Controller();
			List<Group> grp = conn.getListGroupByUserId(userId, fieldId);
			if (grp.size() < 1 || grp == null) {
				System.out.println("listGrp null " + userId);
				return new RetValData(ErrorCode.Not_OK.code, "",
						"list grp null");
			}
			JSONArray arrLisrGrp = new JSONArray();
			for (Group item : grp) {
				JSONObject obj = new JSONObject();

				try {
					DecimalFormat df2 = new DecimalFormat(
							"###,###,###,###,###,###,###.##");
					obj.put("groupId", item.getGroupId());
					String groupName = item.getGroupName();
					/*
					 * String[] parts = groupName.split(" "); if (parts.length >
					 * 6) { groupName = ""; for (int i = 0; i < 7; i++) { if (i
					 * != 6) groupName += parts[i] + " "; else groupName +=
					 * parts[i] + " ..."; } }
					 */

					obj.put("groupName", groupName);
					if (item.getSttcId() == 0) {
						obj.put("sttcId", "");
						obj.put("sttcName", "");
						obj.put("sttcRealityValue", "");
						obj.put("sttGrowthState", 1);
						// obj.put("sttcTypeTime", sttc.getTypeTime());
						obj.put("sttcType", "");
						obj.put("sttcKpiState", 1);
						obj.put("sttcTime", "");
						obj.put("sttcGrowthValue", "");
						obj.put("sttcKpiValue", "");
						obj.put("sttcUnitOfGrowthValue", "");
						obj.put("datasource", "");
					} else {
						System.out.println("item.getSttcId() "
								+ item.getSttcId());
						Statistical sttc = conn.get(Statistical.class,
								item.getSttcId());
						obj.put("sttcId", sttc.getId());
						obj.put("sttcName", sttc.getName());
						obj.put("sttcRealityValue",
								Float.valueOf(sttc.getRealityValue()));
						obj.put("sttGrowthState", sttc.getGrowthState());
						// obj.put("sttcTypeTime", sttc.getTypeTime());
						obj.put("sttcType", sttc.getTypeTime());
						obj.put("sttcKpiState", sttc.getKpiState());
						obj.put("sttcTime", ctrl.convertTypeTimeToDate(
								Integer.valueOf(sttc.getTypeTime()),
								sttc.getTime()));
						obj.put("sttcGrowthValue",
								df2.format(sttc.getGrowthValue()));
						obj.put("sttcKpiValue", df2.format(sttc.getKpiValue()));
						obj.put("sttcUnitOfGrowthValue",
								sttc.getUnitOfGrowthValue());
						obj.put("datasource", sttc.getDatasource());
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				arrLisrGrp.put(obj);
			}

			User user = conn.get(User.class, userId);
			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.Not_OK.code, "", "userId null");

			return new RetValData(ErrorCode.OK.code, arrLisrGrp.toString(), "");
		}

		System.out.println("listGrp - userId null");
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	public RetValData getListGroupByUser(String userId, int fieldId) {

		System.out.println("Call getListGroupByUser");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("fieldId: " + fieldId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			List<Group> grp = conn.getListGroupByUserId(userId, fieldId);
			if (grp.size() < 1 || grp == null) {
				System.out.println("listGrp null " + userId);
				return new RetValData(ErrorCode.Not_OK.code, "",
						"list grp null");
			}
			JSONArray arrLisrGrp = new JSONArray();
			for (Group item : grp) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("groupId", item.getGroupId());
					String groupName = item.getGroupName();

					String[] parts = groupName.split(" ");
					if (parts.length > 6) {
						groupName = "";
						for (int i = 0; i < 6; i++) {
							if (i != 5)
								groupName += parts[i] + " ";
							else
								groupName += parts[i] + " ...";
						}
					}

					obj.put("groupName", groupName);
					if (item.getSttcId() == 0) {
						obj.put("sttcId", "");
						obj.put("sttcName", "");
						obj.put("time", "");
					} else {
						Statistical tk = conn.get(Statistical.class,
								item.getSttcId());
						obj.put("sttcId", tk.getId());
						obj.put("sttcName", tk.getName());
						obj.put("time", tk.getTime());
					}

				} catch (JSONException e) {
					System.out.println("JSONException");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				arrLisrGrp.put(obj);
			}

			return new RetValData(ErrorCode.OK.code, arrLisrGrp.toString(), "");
		}
		System.out.println("listGrp - userId null");
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	public RetValData getGroup(String userId, int grpId) {
		System.out.println("Call getGroup");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("grpId: " + grpId);
		}
		if (grpId > 0) {
			JSONObject obj = new JSONObject();
			MySqlConnection con = new MySqlConnection();
			Group grp = con.get(Group.class, grpId);
			try {
				obj.put("groupId", grp.getGroupId());
				obj.put("fieldId", grp.getGroupId());
				obj.put("groupName", grp.getGroupId());
				obj.put("sttcId", grp.getSttcId());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new RetValData(ErrorCode.Not_OK.code, "",
						"JSONException");
			}
			return new RetValData(ErrorCode.OK.code, obj.toString(), "");
		}
		return new RetValData(ErrorCode.Not_OK.code, "", "userId null");
	}

	public RetValData addGroup(String userId, String jsonParam) {
		System.out.println("Call addGroup");
		if (iWriteLog) {
			System.out.println("userId: " + userId);

		}
		/*
		 * if (Utility.isEmptyOrNull(group.getGroupName()) ||
		 * Utility.isEmptyOrNull(group.getUserId())) { return new
		 * RetValData(ErrorCode.Invalid_Input.code, "",
		 * "groupName or userID NULL"); }
		 */

		MySqlConnection con = new MySqlConnection();
		// User user = con.get(User.class, group.getUserId());
		if (userId == null) {
			return new RetValData(ErrorCode.User_Not_Existed.code);
		}

		/*
		 * if (user.getLoginState() != 1) return new
		 * RetValData(ErrorCode.User_Not_Login.code);
		 * 
		 * if (user.getPermission() == 1) group.setUserId("admin"); // get
		 * tablelist long countOfBITable =
		 * con.countRecordOfTable(BITable.class);
		 * 
		 * if (Utility.isEmptyOrNull(group.getTableList())) { String
		 * listTablesId = ""; for (long i = 2; i <= countOfBITable; i++) // bo
		 * bang dau tien { if (i == countOfBITable) listTablesId +=
		 * Long.toString(i); else listTablesId = listTablesId + Long.toString(i)
		 * + "."; } group.setTableList(listTablesId); } else { // TODO: check
		 * tablelist valid }
		 */

		try {
			JSONObject obj = new JSONObject(jsonParam);
			String groupName = obj.getString("groupName");
			int fieldId = Integer.valueOf(obj.getString("fieldId"));
			Group group = new Group();
			group.setDescription(groupName);
			group.setTableList("");
			group.setSttcId(1);
			group.setFieldId(fieldId);
			group.setUserId(userId);
			group.setGroupName(groupName);
			if (con.insert(group) == ErrorCode.OK.code) {
				// sleep for hibernate.
				// TODO: synchronize
				Controller ctrl = new Controller();
				DashboardConfig dbcf = con.getDashboardConfig(
						group.getUserId(), group.getFieldId());
				List<String> arrConfig = ctrl.readConfigData(dbcf
						.getConfigData());
				String[] arr = arrConfig.get(0).split(" ");
				int newNumberOfGroup = Integer.parseInt(arr[1]) + 1;
				String newLine0 = arr[0] + " "
						+ Integer.toString(newNumberOfGroup);
				String newConfigData = newLine0 + "\n";
				for (int i = 1; i < arrConfig.size(); i++)
					newConfigData = newConfigData + arrConfig.get(i) + "\n";
				newConfigData = newConfigData
						+ Integer.toString(group.getGroupId()) + " 0 0";
				dbcf.setConfigData(Base64Utils.base64Encode(newConfigData
						.getBytes()));
				con.update(DashboardConfig.class, dbcf);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new RetValData(ErrorCode.OK.code, Integer.toString(group
						.getGroupId()), "");
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new RetValData(ErrorCode.Not_OK.code, "", "JSONException");
		}

		return new RetValData(ErrorCode.Not_OK.code, "", "cannot insert group");
	}

	/*
	 * public Group getGroup(int groupID) { MySqlConnection con = new
	 * MySqlConnection(); Group nhom = con.get(Group.class, groupID); User user
	 * = con.get(User.class, nhom.getUserId()); if (user.getLoginState() == 1)
	 * return nhom; return null; }
	 */

	/*
	 * public RetValData updateGroup(Group group) { if
	 * (Utility.isEmptyOrNull(group.getUserId()) ||
	 * Utility.isEmptyOrNull(group.getGroupName())) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "",
	 * "user id or group name invalid"); } MySqlConnection con = new
	 * MySqlConnection(); int ret = con.update(Group.class, group);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "update user fail"); } } public
	 * RetValData deleteGroup(int groupId) { if (groupId < 0) return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "groupID < 0");
	 * MySqlConnection con = new MySqlConnection(); int ret =
	 * con.delete(Group.class, groupId); if (ret == ErrorCode.OK.code){ return
	 * new RetValData(ErrorCode.OK.code); } else { return new RetValData(ret,
	 * "", "delete group fail"); } }
	 */
	@SuppressWarnings("unused")
	private final int _____statistical_____ = 0;

	// public static RetValData addStatistical(String userId, String jsonObj)
	// {// Tra
	// // ve
	// // thong
	// // ke id
	// Statistical sttc = new Statistical();
	// try {
	// JSONObject obj = new JSONObject(jsonObj);
	// sttc.setGroupId(Integer.valueOf(obj.getString("groupId")));
	// sttc.setName(obj.getString("sttcName"));
	// sttc.setRealityValue(Integer.valueOf(obj.getString("realityValue")));
	// sttc.setUnitOfRealityValue(obj.getString("unitOfRealityValue"));
	// sttc.setKpiValue(Integer.valueOf(obj.getString("kpiValue")));
	// sttc.setTypeTime(Integer.valueOf(obj.getString("timeType")));
	// sttc.setUnitOfGrowthValue((obj.getString("unitOfGrowthValue")));
	// int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
	// if ((sttc.getRealityValue() / currentMonth) > (sttc.getKpiValue() / 12))
	// sttc.setKpiState(0);
	// else
	// sttc.setKpiState(1);
	// sttc.setProgressValue(0);
	// Date date = new Date();
	// SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	// sttc.setTime(fm.format(date));
	// sttc.setDatasource("Cục Viễn thông");
	// sttc.setType(0);
	// sttc.setGrowthState(1);
	// sttc.setUserId(userId);
	// } catch (JSONException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// return new RetValData(ErrorCode.Invalid_Input.code, "N/A",
	// "JSONException");
	// }
	//
	// if (Utility.isEmptyOrNull(sttc.getUserId())
	// || Utility.isEmptyOrNull(sttc.getName())
	// || sttc.getGroupId() <= 0 || sttc.getType() < 0
	// || sttc.getType() > 3 || sttc.getTypeTime() <= 0
	// || sttc.getRealityValue() < 0
	// || Utility.isEmptyOrNull(sttc.getUnitOfRealityValue())) {
	// return new RetValData(
	// ErrorCode.Invalid_Input.code,
	// "",
	// "param(userID, groupID, type, typeTime, realityValue, realityValue unit) invalid");
	// }
	//
	// // TODO: xem lai phan nay
	// if (sttc.getType() == 0 && sttc.getKpiValue() < 0)
	// return new RetValData(ErrorCode.Invalid_Input.code, "",
	// "type and kpi not match");
	// MySqlConnection con = new MySqlConnection();
	// Group group = con.get(Group.class, sttc.getGroupId());
	// User user = con.get(User.class, sttc.getUserId());
	// if (group == null) {
	// return new RetValData(ErrorCode.Object_Not_Existed.code, "",
	// "group not exist");
	// }
	// if (user == null) {
	// return new RetValData(ErrorCode.User_Not_Existed.code, "",
	// "user not exist");
	// }
	//
	// if (user.getPermission() == 1 && !group.getUserId().equals("admin")) //
	// Admin
	// // user
	// {
	// return new RetValData(
	// ErrorCode.Admin_Cannot_Insert_User_Group.code, "",
	// "user permission not match");
	// }
	//
	// if (user.getLoginState() != 1)
	// return new RetValData(ErrorCode.User_Not_Login.code, "",
	// "user not login");
	//
	// if (user.getPermission() == 1) {
	// sttc.setUserId("admin");
	// }
	//
	// int ret = con.insert(sttc);
	// if (ret != ErrorCode.OK.code) {
	// return new RetValData(ErrorCode.Not_OK.code, "",
	// "cannot insert statistical");
	// }
	//
	// // TODO: remove sleep
	// try {
	// Thread.sleep(500);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// /*
	// * System.out.println("Add Sttc OK " + sttc.getId());
	// *
	// * Controller ctr = new Controller(); int checkAddDashboard =
	// * ctr.addEmptyDashboard_Resource(
	// * Utility.convertStringSignToNoSign(sttc.getName()),
	// * Utility.convertStringSignToNoSign(sttc.getName()), "",
	// * sttc.getUserId(), sttc.getId()); if (checkAddDashboard !=
	// * ErrorCode.OK.code) return new RetValData(ErrorCode.Not_OK.code, "",
	// * "add dashboard resource fail:" + checkAddDashboard);
	// */
	// return new RetValData(ErrorCode.OK.code,
	// Integer.toString(sttc.getId()), "");
	// }
	public RetValData addStatistical(String userId, String jsonObj) {
		System.out.println("Call addStatistical");
		if (iWriteLog) {
			System.out.println("jsonParam: " + jsonObj);
		}
		try {
			JSONObject obj = new JSONObject(jsonObj);
			int fieldId = Integer.valueOf(obj.getString("fieldId"));
			int groupId = Integer.valueOf(obj.getString("groupId"));
			String sttcName = obj.getString("sttcName");
			float kpiValue = Float.valueOf(obj.getString("kpiValue"));
			float realityValue = Float.valueOf(obj.getString("realityValue"));
			String unitOfRealityValue = obj.getString("unitOfRealityValue");
			int typeTime = Integer.valueOf(obj.getString("typeTime"));
			String unitOfGrowthValue = obj.getString("unitOfGrowthValue");
			String iconUrl = obj.getString("iconUrl");
			String formula = obj.getString("formula");
			String datasource = obj.getString("dataSource");
			System.out.println("formula: " + formula);
			SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date = new Date();

			Statistical sttc = new Statistical();
			sttc.setGroupId(groupId);
			sttc.setKpiValue(kpiValue);
			sttc.setRealityValue(realityValue);
			sttc.setUnitOfGrowthValue(unitOfGrowthValue);
			sttc.setUnitOfRealityValue(unitOfRealityValue);
			sttc.setTypeTime(typeTime);
			sttc.setName(sttcName);
			sttc.setType(1);
			sttc.setDatasource(datasource);
			sttc.setGrowthState(0);
			sttc.setTime(fm.format(date));
			sttc.setProgressValue(100);
			sttc.setUserId(userId);
			sttc.setGrowthState(0);
			sttc.setGrowthValue(0);
			MySqlConnection con = new MySqlConnection();
			int result = con.insert(sttc);

			if (result == 0) {
				// update config
				Controller ctrl = new Controller();
				DashboardConfig dbcf = con.getDashboardConfig(userId, fieldId);
				List<String> configArr = ctrl.readConfigData(dbcf
						.getConfigData());
				String newConfigData = "";
				String[] line1 = configArr.get(0).split(" ");
				int numOfGroup = Integer.parseInt(line1[1]);
				boolean found = false;
				for (int i = 1; i < configArr.size(); i++) {
					String[] arrLineCf = configArr.get(i).split(" ");
					if (Integer.parseInt(arrLineCf[0]) == groupId) {
						found = true;
						int newNumOfSttc = Integer.parseInt(arrLineCf[1]) + 1;
						String newConfigLine = arrLineCf[0] + " "
								+ Integer.toString(newNumOfSttc) + " "
								+ arrLineCf[2] + "." + sttc.getId();
						if (i == configArr.size() - 1)
							newConfigData = newConfigData + newConfigLine;
						else
							newConfigData = newConfigData + newConfigLine
									+ "\n";
					} else if ((i == configArr.size() - 1) && found)
						newConfigData = newConfigData + configArr.get(i);
					else
						newConfigData = newConfigData + configArr.get(i) + "\n";
				}
				if (!found) {
					System.out.println("add sttc in new group");
					numOfGroup++;
					String newConfigLine = Integer.toString(groupId) + " 1 "
							+ Integer.toString(sttc.getId());
					System.out.println(newConfigLine);
					newConfigData = newConfigData + newConfigLine;
					System.out.println(newConfigData);
				}
				newConfigData = line1[0] + " " + Integer.toString(numOfGroup)
						+ "\n" + newConfigData;

				dbcf.setConfigData(Base64Utils.base64Encode(newConfigData
						.getBytes()));
				con.update(DashboardConfig.class, dbcf);
				// Create formula
				Formula f = new Formula();
				BISQLController ctr = new BISQLController();
				JSONObject objFormula = new JSONObject(formula);
				RetValData ret = ctr.genStatisSQL(objFormula.getString("view"),
						objFormula.getString("data"));
				if (ret.getCode() != ErrorCode.OK.code) {
					System.out.println(ret);
					return ret;
				}
				String sql = ret.getData();
				f.setSttcId(sttc.getId());
				f.setSql(sql);
				f.setType(1);
				f.setJsonParam(formula);
				result = con.insert(f);
				return new RetValData(ErrorCode.OK.code, "",
						String.valueOf(result));
			} else
				return new RetValData(ErrorCode.Not_OK.code,
						String.valueOf(result), "insert fail");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RetValData(ErrorCode.Not_OK.code);
		}
	}

	public RetValData updateStatistical(String userId, int sttcId,
			String jsonObj) {
		System.out.println("Call updateStatistical");
		if (iWriteLog) {
			System.out.println("sttcId: " + sttcId);
			System.out.println("jsonParam: " + jsonObj);
		}
		try {
			JSONObject obj = new JSONObject(jsonObj);
			int fieldId = Integer.valueOf(obj.getString("fieldId"));
			int groupId = Integer.valueOf(obj.getString("groupId"));
			String sttcName = obj.getString("sttcName");
			float kpiValue = Float.valueOf(obj.getString("kpiValue"));
			float realityValue = Float.valueOf(obj.getString("realityValue"));
			String unitOfRealityValue = obj.getString("unitOfRealityValue");
			int typeTime = Integer.valueOf(obj.getString("typeTime"));
			String unitOfGrowthValue = obj.getString("unitOfGrowthValue");
			String iconUrl = obj.getString("iconUrl");
			String formula = obj.getString("formula");
			String datasource = obj.getString("dataSource");
			System.out.println("formula: " + formula);
			SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date = new Date();

			Statistical sttc = new Statistical();
			sttc.setId(sttcId);
			sttc.setGroupId(groupId);
			sttc.setKpiValue(kpiValue);
			sttc.setRealityValue(realityValue);
			sttc.setUnitOfGrowthValue(unitOfGrowthValue);
			sttc.setUnitOfRealityValue(unitOfRealityValue);
			sttc.setTypeTime(typeTime);
			sttc.setName(sttcName);
			sttc.setType(1);
			sttc.setDatasource(datasource);
			sttc.setGrowthState(0);
			sttc.setTime(fm.format(date));
			sttc.setProgressValue(100);
			sttc.setUserId(userId);
			sttc.setGrowthState(0);
			sttc.setGrowthValue(0);
			MySqlConnection con = new MySqlConnection();
			int result = con.update(Statistical.class, sttc);

			if (result == 0) {
				// Update config data
				DashboardConfig dbcf = con.getDashboardConfig(userId, fieldId);
				Controller ctrl = new Controller();
				List<String> configData = ctrl.readConfigData(dbcf
						.getConfigData());
				int lineIndex = 0;
				String newLineConfig = "";
				for (int i = 1; i < configData.size(); i++) {
					lineIndex = i;
					String[] lineConfigArr = configData.get(i).split(" ");
					if (Integer.parseInt(lineConfigArr[0]) == groupId) {
						int newNumberSttc = Integer.parseInt(lineConfigArr[1]) + 1;
						String newArrSttc = lineConfigArr[2] + "."
								+ sttc.getId();

						newLineConfig = lineConfigArr[0] + " " + newNumberSttc
								+ " " + newArrSttc;
						break;
					}
				}
				String newConfig = "";
				for (int i = 0; i < configData.size(); i++) {
					if (i == lineIndex)
						newConfig += newLineConfig;
					else
						newConfig += configData.get(i);
				}
				dbcf.setConfigData(Base64Utils.base64Encode(newConfig
						.getBytes()));
				con.update(DashboardConfig.class, dbcf);
				// Add formula
				Formula f = new Formula();
				BISQLController ctr = new BISQLController();
				JSONObject objFormula = new JSONObject(formula);
				RetValData ret = ctr.genStatisSQL(objFormula.getString("view"),
						objFormula.getString("data"));
				if (ret.getCode() != ErrorCode.OK.code) {
					System.out.println(ret);
					return ret;
				}
				String sql = ret.getData();
				Formula fml = con.getFormulaBySttc(sttcId);
				f.setSttcId(sttc.getId());
				f.setSql(sql);
				f.setType(fml.getType());
				f.setId(fml.getId());
				f.setJsonParam(formula);
				result = con.insert(f);
				return new RetValData(ErrorCode.OK.code, "",
						String.valueOf(result));
			} else
				return new RetValData(ErrorCode.Not_OK.code,
						String.valueOf(result), "insert fail");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RetValData(ErrorCode.Not_OK.code);
		}
	}

	public RetValData getListDescriptionOfBIColumn(String jsonParam) {
		System.out.println("Call getListDescriptionOfBIColumn");
		if (iWriteLog) {
			System.out.println("jsonParam: " + jsonParam);
		}
		Controller ctrl = new Controller();
		String data = ctrl.getDescriptionOfBIColumn(jsonParam);
		// System.out.println("data "+ data);
		if (data == "")
			return new RetValData(ErrorCode.Not_OK.code);
		else
			return new RetValData(ErrorCode.OK.code, data, "");
	}

	public RetValData getDataFilter() {
		System.out.println("Call getDataFilter");

		Controller ctrl = new Controller();
		RetValData data = ctrl.getFilter();
		
		return data;
	}

	public RetValData getAllStatisticalOfGroup(String userId, int groupId) {
		System.out.println("Call getAllStatisticalOfGroup");
		MySqlConnection con = new MySqlConnection();
		List<Statistical> listStcc = con.getAllStatisticalOfGroup(groupId);
		JSONArray arr = new JSONArray();
		for (Statistical sttc : listStcc) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("sttcId", sttc.getId());
				obj.put("sttcName", sttc.getName());
				arr.put(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new RetValData(ErrorCode.Not_OK.code, "",
						"JSONException");
			}
		}
		return new RetValData(ErrorCode.OK.code, arr.toString(), "");
	}

	public RetValData getStatistical(int sttcId) {
		System.out.println("Call getStatistical");
		MySqlConnection con = new MySqlConnection();
		Statistical tk = con.get(Statistical.class, sttcId);
		User user = con.get(User.class, tk.getUserId());
		if (user.getLoginState() != 1)
			return new RetValData(ErrorCode.User_Not_Login.code);

		JSONObject obj = new JSONObject();
		Group grp = con.get(Group.class, tk.getGroupId());
		try {
			obj.put("fieldId", grp.getFieldId());
			obj.put("groupId", tk.getGroupId());
			obj.put("sttcName", tk.getName());
			obj.put("kpiValue", tk.getKpiValue());
			obj.put("realityValue", tk.getRealityValue());
			obj.put("unitOfRealityValue", tk.getUnitOfRealityValue());
			obj.put("unitOfGrowthValue", tk.getUnitOfGrowthValue());
			obj.put("iconUrl", "");
			Formula fml = con.getFormulaBySttc(sttcId);
			obj.put("formula", fml.getJsonParam());
			obj.put("dataSource", tk.getDatasource());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RetValData(ErrorCode.Not_OK.code, "", "JSONException");
		}
		return new RetValData(ErrorCode.OK.code, obj.toString(), "");

	}

	public RetValData getListStatisticalOfUser(String userId, int fieldId) {
		// List<String> rs = new ArrayList<>();
		System.out.println("Call getListStatisticalOfUser");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("fieldId: " + fieldId);
		}
		if (Utility.isEmptyOrNull(userId)) {
			System.out.println("Invalid input data");
			return new RetValData(ErrorCode.Not_OK.code);
		}

		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null) {
			System.out.println("User not existed");
			return new RetValData(ErrorCode.User_Not_Existed.code);
		}
		if (user.getLoginState() != 1) {
			System.out.println("user not login");
			return new RetValData(ErrorCode.Not_OK.code);
		}
		Controller ctr = new Controller();
		String rs = ctr.getDashBoardDataOfDetailUser(user, fieldId);
		return new RetValData(ErrorCode.OK.code, rs.toString(), "");
	}

	
	public RetValData getListStatisticalOfGroup(String userId, int fieldId,
			int groupID) {
		System.out.println("Call getListStatisticalOfGroup");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("fieldId: " + fieldId);
			System.out.println("groupID: " + groupID);
		}
		List<String> rs = new ArrayList<>();
		if (Utility.isEmptyOrNull(userId)) {
			System.out.println("Invalid input data");
			return new RetValData(ErrorCode.Not_OK.code);
		}

		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null) {
			System.out.println("User not existed");
			return new RetValData(ErrorCode.User_Not_Existed.code);
		}
		if (user.getLoginState() != 1) {
			System.out.println("user not login");
			return new RetValData(ErrorCode.Not_OK.code);
		}

		Controller ctr = new Controller();
		rs = ctr.getDashBoardData(user, fieldId, groupID);// Sttc null by Id

		return new RetValData(ErrorCode.OK.code, rs.toString(), "");
	}

	public RetValData getListSttcPagingForUser(String userId, int start, int max) {
		System.out.println("Call getListSttcPagingForUser");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("start: " + start);
			System.out.println("max: " + max);
		}

		if (Utility.isEmptyOrNull(userId)) {
			System.out.println("Invalid input data");
			return new RetValData(ErrorCode.Not_OK.code);
		}

		Controller ctr = new Controller();
		String jsonData = ctr.getListSttcPagingForUser(userId, start, max);
		return new RetValData(ErrorCode.OK.code, jsonData, "");
	}

	/*
	 * public List<Statistical> getListStatisticalOfGroup(int groupID) {
	 * MySqlConnection con = new MySqlConnection(); List<Statistical> list = new
	 * ArrayList<>(); if (con.get(Group.class, groupID) == null) {
	 * System.err.println("group null"); return list; } return
	 * con.getListFK_StatisticalOfGroup(groupID); }
	 */
	/*
	 * public RetValData updateStatistical(Statistical sttc) { if
	 * (Utility.isEmptyOrNull(sttc.getUserId()) ||
	 * Utility.isEmptyOrNull(sttc.getName()) || sttc.getKpiValue() < 0 ||
	 * sttc.getGroupId() <= 0 || sttc.getType() <= 0 || sttc.getTypeTime() <= 0
	 * ) { return new RetValData(ErrorCode.Invalid_Input.code, "",
	 * "userid or kpi or group id or type or type time invalid"); }
	 * MySqlConnection con = new MySqlConnection(); int ret =
	 * con.update(Statistical.class, sttc);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "update user fail"); } }
	 */
	/*
	 * public RetValData deleteStatistical(int sttcId) { if (sttcId < 0) return
	 * new RetValData(ErrorCode.Invalid_Input.code, "", "sttcId < 0");
	 * MySqlConnection con = new MySqlConnection(); int ret =
	 * con.delete(Statistical.class, sttcId); if (ret == ErrorCode.OK.code){
	 * return new RetValData(ErrorCode.OK.code); } else { return new
	 * RetValData(ret, "", "delete statistical fail"); } }
	 */

	@SuppressWarnings("unused")
	private final int _____formula_____ = 0;

	// public RetValData addFormula(Formula formula, String listColumnId) {
	// System.out.println("Add formula");
	// System.out.println("listColumnId: " + listColumnId);
	// System.out.println("for sql: " + formula.getSql());
	// System.out.println("sttd Id: " + formula.getSttcId());
	// if (Utility.isEmptyOrNull(formula.getSql())
	// || Utility.isEmptyOrNull(formula.getUnit())
	// || formula.getSttcId() <= 0) {
	// System.out.println("invalid input");
	// return new RetValData(ErrorCode.Invalid_Input.code, "",
	// "sql or unit NULL or sttcID < 0");
	// }
	//
	// MySqlConnection con = new MySqlConnection();
	// Statistical sttc = con.get(Statistical.class, formula.getSttcId());
	// User user = con.get(User.class, sttc.getUserId());
	//
	// if (user == null) {
	// System.out.println("null user");
	// return new RetValData(ErrorCode.User_Not_Existed.code, "", "user null");
	// }
	//
	// if (user.getLoginState() != 1){
	// System.out.println("no login");
	// return new RetValData(ErrorCode.User_Not_Login.code, "",
	// "user has not loged in");
	// }
	//
	// //generate sql
	//
	// String[] listCol = listColumnId.split("\\.");
	// if(listCol.length == 0) listCol[0] = listColumnId;
	// List<Integer> listTableId = new ArrayList<Integer>();
	// for(String s : listCol)
	// {
	// BIColumn col = con.get(BIColumn.class, Integer.parseInt(s));
	// if(col ==null) return new RetValData(ErrorCode.Object_Not_Existed.code,
	// "", "BIColumn NULL");
	// listTableId.add(col.getTableID());
	// }
	//
	//
	// Set<Integer> set = new HashSet<>(listTableId);
	// listTableId.clear();
	// listTableId.addAll(set);
	// List<String> listTable = new ArrayList<String>();
	// for(int s : listTableId)
	// {
	// BITable table = con.get(BITable.class, s);
	// //System.out.println(table.getName());
	// if(table == null) return new
	// RetValData(ErrorCode.Object_Not_Existed.code, "", "BITable NULL");
	// listTable.add(table.getName());
	// }
	//
	// for(String s : listTable) System.out.println(s);
	//
	// String sql = formula.getSql();
	// int asPos = sql.indexOf(" as ");
	// String fml = sql.substring(7, asPos);
	// Controller ctr = new Controller();
	// BISQLController biCtr = new BISQLController();
	// RetValData retSqlTmp = biCtr.genBusinessQueryFrom(listTable);
	// System.out.println("Sql get doanh nghiep: " + retSqlTmp.getData());
	//
	// List<String> listBusiness = con.executeNativeSQL(retSqlTmp.getData());
	//
	// System.out.println("Ten doanh nghiep");
	// for(String s : listBusiness) System.out.println(s);
	//
	// RetValData retSqlWidget = biCtr.genQueryFrom(listTable, fml,
	// sttc.getTypeTime(), listBusiness);
	// System.out.println("sql: " + retSqlWidget.getData());
	//
	// //end generate sql
	// //add widget
	// long id = con.countRecordOfTable(Widget_Resource.class) + 1;
	// String widgetId =
	// Long.toString(id)+"_"+Utility.convertStringSignToNoSign(sttc.getName());
	// if(con.createViewsTable(widgetId, retSqlWidget.getData()) != 0)
	// {
	// return new RetValData(ErrorCode.Create_View_Table_Fail.code, "",
	// "create view fail");
	// }
	// int checkAddWidget = ctr.addWidget_Resource(Define.Bar_Chart.code,
	// widgetId,
	// "select * from " + widgetId, sttc.getTypeTime(),
	// widgetId,listBusiness, formula.getSttcId());
	//
	// if(checkAddWidget != ErrorCode.OK.code) return new
	// RetValData(ErrorCode.Not_OK.code, "", "add widget resource fail");
	//
	// int checkAddDashboard = ctr.addDashboard_Resource(widgetId,
	// sttc.getUserId(), widgetId, formula.getSttcId());
	// if(checkAddDashboard != ErrorCode.OK.code) return new
	// RetValData(ErrorCode.Not_OK.code, "", "add dashboard resource fail");
	//
	// //end addwidget
	//
	// //Create default chartObject:
	// ChartObject chart = new ChartObject();
	// chart.setChartColumnList(listColumnId);
	// chart.setChartName(widgetId);
	// chart.setSttcId(formula.getSttcId());
	// chart.setEndDate("");
	// chart.setStartedDate("");
	// chart.setTypeTime(sttc.getTypeTime());
	// chart.setUrlWebView("https://mic.bkav.com:9643/portal/dashboards/"+widgetId+"/home");
	// if(con.insert(chart) == ErrorCode.Not_OK.code) return new
	// RetValData(ErrorCode.Not_OK.code, "", "insert chart fail");
	// if (con.insert(formula) != ErrorCode.OK.code) return new
	// RetValData(ErrorCode.Not_OK.code, "", "insert formula fail");
	// return new RetValData(ErrorCode.OK.code,
	// Integer.toString(formula.getId()), "");
	// }

	public RetValData createFormula(String jsonParam) {
		System.out.println("createFormula jsonParam: " + jsonParam);
		Controller conn = new Controller();
		return conn.createFormula(jsonParam);
	}

	public RetValData getDatafromFormula(String jsonParam) {
		MySqlConnection conn = new MySqlConnection();
		BISQLController ctr = new BISQLController();
		try {
			System.out.println("jsonParam: " + jsonParam);
			JSONObject json = new JSONObject(jsonParam);
			RetValData ret = ctr.genStatisSQL(json.getString("view"),
					json.getString("data"));
			if (ret.getCode() != ErrorCode.OK.code) {
				System.out.println(ret);
				return ret;
			}
			String sql = ret.getData();
			System.out.println("SQL: " + sql);
			
			//String data = conn.executeNativeSQL(sql);
			String data = conn.getColumnResult(sql);
			System.out.println("Data  " + data);
			RetValData dataRet = Controller.getDataFromSQLResultColumn(
					json.getJSONObject("view").getString("xAxis"),
					json.getJSONObject("data").getString("alias"),
					data);
			System.out.println("Data  " + dataRet);
			
			return dataRet;
			// Random rd = new Random();
			// int x = rd.nextInt(100);
			// return new RetValData(ErrorCode.OK.code,String.valueOf(x) , "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RetValData(ErrorCode.Invalid_Input.code, e.getMessage(),
					"json invalid");
		}

	}

	// public RetValData createChart(String user, int chartType, String
	// startTime, String endTime, int timeType, int statisticalID, List<String>
	// listChartColumn, String yAxisLabel){
	// if (Utility.isEmptyOrNull(user)){
	// return new RetValData(ErrorCode.Invalid_Input.code, "",
	// "user null or empty");
	// }
	//
	// if (listChartColumn.size() == 0){
	// return new RetValData(ErrorCode.Invalid_Input.code, "",
	// "listChartColumn");
	// }
	// List<BIColumnInChart> biColumnList = new ArrayList<BIColumnInChart>();
	// List<String> presentColumn = new ArrayList<String>();
	// try {
	// for (String chartColumn: listChartColumn){
	// JSONObject tempJson = new JSONObject(chartColumn);
	// BIColumnInChart temp = new BIColumnInChart();
	// temp.setBusinessID(tempJson.getInt("businessID"));
	// temp.setColumnName(tempJson.getString("columnName"));
	// temp.setPresentName(tempJson.getString("presentName"));
	// temp.setRegionalID(tempJson.getInt("regionalID"));
	// temp.setTableName(tempJson.getString("tableName"));
	//
	// biColumnList.add(temp);
	//
	// presentColumn.add(tempJson.getString("presentName"));
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return new RetValData(ErrorCode.Invalid_Input.code, "",
	// "json format exception");
	// }
	//
	// BISQLController bisql = new BISQLController();
	// RetValData ret = bisql.genSQLFormulaForChart(startTime, endTime,
	// timeType, biColumnList);
	//
	// if (ret.getCode() != ErrorCode.OK.code){
	// return ret;
	// }
	// System.out.println(ret.getData());
	//
	// MySqlConnection con = new MySqlConnection();
	// Controller ctr = new Controller();
	// //get statistical
	// Statistical sttc = con.get(Statistical.class, statisticalID);
	// if (sttc == null){
	// return new RetValData(ErrorCode.Object_Not_Existed.code, "",
	// "Statistical null");
	// }
	//
	// //add widget
	// long id = con.countRecordOfTableInWSO2(Widget_Resource.class) + 1;
	// String widgetId =
	// Long.toString(id)+"_"+Utility.convertStringSignToNoSign(sttc.getName());
	// if(con.createViewsTable(widgetId, ret.getData()) != 0)
	// {
	// return new RetValData(ErrorCode.Create_View_Table_Fail.code, "",
	// "create view fail");
	// }
	// int checkAddWidget = ctr.addWidget_Resource(chartType, widgetId,
	// "select * from " + widgetId, timeType,
	// widgetId, presentColumn, statisticalID, yAxisLabel);
	//
	// if(checkAddWidget != ErrorCode.OK.code) return new
	// RetValData(ErrorCode.Not_OK.code, "", "add widget resource fail:" +
	// checkAddWidget);
	//
	// int checkAddDashboard = ctr.addDashboard_Resource(widgetId,
	// sttc.getUserId(), widgetId, statisticalID);
	// if(checkAddDashboard != ErrorCode.OK.code) return new
	// RetValData(ErrorCode.Not_OK.code, "", "add dashboard resource fail:" +
	// checkAddDashboard);
	// //end addwidget
	//
	// //Create default chartObject:
	// ChartObject chart = new ChartObject();
	// chart.setChartColumnList(listChartColumn.toString());
	// chart.setChartName(widgetId);
	// chart.setSttcId(statisticalID);
	// chart.setEndDate(startTime);
	// chart.setStartedDate(endTime);
	// chart.setTypeTime(timeType);
	// chart.setUrlWebView("https://mic.bkav.com:9643/portal/dashboards/"+widgetId+"/home");
	// if(con.insert(chart) == ErrorCode.Not_OK.code){
	// return new RetValData(ErrorCode.Create_Chart_Fail.code, "",
	// "insert chart fail");
	// }
	// return new RetValData(ErrorCode.OK.code, chart.getUrlWebView(), "");
	// }

	/*
	 * 
	 * Cập nhật từ anh TungVtd
	 */

	/*
	 * public RetValData createChart(String user, int chartType, String
	 * startTime, String endTime, int timeType, int statisticalID, List<String>
	 * listChartColumn, String chartName, String yAxisLabel) { if
	 * (Utility.isEmptyOrNull(user)) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "user null or empty"); }
	 * 
	 * if (listChartColumn.size() == 0) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "listChartColumn"); }
	 * List<BIColumnInChart> biColumnList = new ArrayList<BIColumnInChart>();
	 * List<String> presentColumn = new ArrayList<String>(); try { for (String
	 * chartColumn : listChartColumn) { JSONObject tempJson = new
	 * JSONObject(chartColumn); BIColumnInChart temp = new BIColumnInChart();
	 * temp.setBusinessID(tempJson.getInt("businessID"));
	 * temp.setColumnName(tempJson.getString("columnName"));
	 * temp.setPresentName(tempJson.getString("presentName"));
	 * temp.setRegionalID(tempJson.getInt("regionalID"));
	 * temp.setTableName(tempJson.getString("tableName"));
	 * 
	 * biColumnList.add(temp);
	 * 
	 * presentColumn.add(tempJson.getString("presentName")); } } catch
	 * (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return new RetValData(ErrorCode.Invalid_Input.code,
	 * "", "json format exception"); }
	 * 
	 * BISQLController bisql = new BISQLController(); RetValData ret =
	 * bisql.genSQLFormulaForChart(startTime, endTime, timeType, biColumnList);
	 * 
	 * if (ret.getCode() != ErrorCode.OK.code) { return ret; }
	 * System.out.println(ret.getData());
	 * 
	 * MySqlConnection con = new MySqlConnection(); Controller ctr = new
	 * Controller(); // get statistical Statistical sttc =
	 * con.get(Statistical.class, statisticalID); if (sttc == null) { return new
	 * RetValData(ErrorCode.Object_Not_Existed.code, "", "Statistical null"); }
	 * 
	 * // add widget long id =
	 * con.countRecordOfTableInWSO2(Widget_Resource.class) + 1; String widgetId
	 * = Long.toString(id) + "_" +
	 * Utility.convertStringSignToNoSign(sttc.getName()); if
	 * (con.createViewsTable(widgetId, ret.getData()) != 0) { return new
	 * RetValData(ErrorCode.Create_View_Table_Fail.code, "",
	 * "create view fail"); } int checkAddWidget =
	 * ctr.addWidget_Resource(chartType, widgetId, "select * from " + widgetId,
	 * timeType, chartName, presentColumn, statisticalID, yAxisLabel);
	 * 
	 * if (checkAddWidget != ErrorCode.OK.code) return new
	 * RetValData(ErrorCode.Not_OK.code, "", "add widget resource fail:" +
	 * checkAddWidget);
	 * 
	 * // int checkAddDashboard = ctr.addDashboard_Resource(widgetId, //
	 * sttc.getUserId(), widgetId, statisticalID); // if(checkAddDashboard !=
	 * ErrorCode.OK.code) return new // RetValData(ErrorCode.Not_OK.code, "",
	 * "add dashboard resource fail:" // + checkAddDashboard);
	 * 
	 * RetValData updateResult = ctr.updateDashboardResource(statisticalID,
	 * user); if (updateResult.getCode() != ErrorCode.OK.code) { return new
	 * RetValData(updateResult.getCode(), "", "updateDashboardResource fail->" +
	 * updateResult.getReason()); } // end addwidget
	 * 
	 * // Create default chartObject: ChartObject chart = new ChartObject();
	 * chart.setChartColumnList(listChartColumn.toString());
	 * chart.setChartName(chartName); chart.setSttcId(statisticalID);
	 * chart.setEndDate(startTime); chart.setStartedDate(endTime);
	 * chart.setTypeTime(timeType);
	 * chart.setUrlWebView("https://mic.bkav.com:9643/portal/dashboards/" +
	 * Utility.convertStringSignToNoSign(sttc.getName()) + "/home"); if
	 * (con.insert(chart) == ErrorCode.Not_OK.code) { return new
	 * RetValData(ErrorCode.Create_Chart_Fail.code, "", "insert chart fail"); }
	 * return new RetValData(ErrorCode.OK.code, chart.getUrlWebView(), ""); }
	 */

	public Formula getFormula(int formulaId) {
		System.out.println("Call getFormula");
		if (iWriteLog) {
			System.out.println("formulaId: " + formulaId);

		}
		MySqlConnection con = new MySqlConnection();
		Formula ct = con.get(Formula.class, formulaId);
		Statistical sttc = con.get(Statistical.class, ct.getSttcId());
		if (sttc == null)
			return null;
		User user = con.get(User.class, sttc.getUserId());
		if (user.getLoginState() == 1)
			return ct;
		return null;
	}

	public RetValData getListChartofSttc(String userId, int sttcId) {
		System.out.println("Call getListChartofSttc");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("sttcId: " + sttcId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			Controller ctrl = new Controller();
			String data = ctrl.getFormulaChartbySttc(sttcId);
			if (data == "")
				return new RetValData(ErrorCode.Not_OK.code);
			else {

				return new RetValData(ErrorCode.OK.code, data, "");
			}
		}

		return new RetValData(ErrorCode.Not_OK.code);
	}

	public RetValData getListChartofSttcRender(String userId, int sttcId) {
		System.out.println("Call getListChartofSttc");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("sttcId: " + sttcId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			Controller ctrl = new Controller();
			String data = ctrl.getFormulaChartbySttcRender(sttcId);
			if (data == "")
				return new RetValData(ErrorCode.Not_OK.code);
			else {

				return new RetValData(ErrorCode.OK.code, data, "");
			}
		}

		return new RetValData(ErrorCode.Not_OK.code);
	}

	public RetValData getListChartDetail(String userId, int chartId) {
		System.out.println("Call getListChartDetail");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("chartId: " + chartId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			Controller ctrl = new Controller();
			String data = ctrl.getFormulaChartDetail(chartId);
			if (data == "")
				return new RetValData(ErrorCode.Not_OK.code);
			else {
				return new RetValData(ErrorCode.OK.code, data, "");
			}
		}

		return new RetValData(ErrorCode.Not_OK.code);
	}

	public RetValData getListChartDetailTemp(String userId, int chartId,
			String advanceQuery) {
		System.out.println("Call getListChartDetail");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("chartId: " + chartId);
		}
		if (userId != "" || userId != null) {
			MySqlConnection conn = new MySqlConnection();
			User user = conn.get(User.class, userId);
			if (user == null) {
				return new RetValData(ErrorCode.User_Not_Existed.code);
			}

			if (user.getLoginState() != 1)
				return new RetValData(ErrorCode.User_Not_Login.code);
			Controller ctrl = new Controller();
			String data = ctrl.getFormulaChartDetailTemp(chartId, advanceQuery);
			if (data == "")
				return new RetValData(ErrorCode.Not_OK.code);
			else {
				return new RetValData(ErrorCode.OK.code, data, "");
			}
		}

		return new RetValData(ErrorCode.Not_OK.code);
	}

	public RetValData updateFormula(Formula fm) {
		if (Utility.isEmptyOrNull(fm.getSql())) {
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"sql or unit fail");
		}
		if (fm.getSttcId() <= 0)
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"sttcId < 0");
		MySqlConnection con = new MySqlConnection();
		int ret = con.update(Formula.class, fm);
		if (ret == ErrorCode.OK.code) {
			return new RetValData(ErrorCode.OK.code);
		} else {
			return new RetValData(ret, "", "update formula fail");
		}
	}

	/*
	 * @SuppressWarnings("unused") private final int _____widget_____ = 0;
	 * public RetValData addWidget(Widget_Resource wg) { if
	 * (Utility.isEmptyOrNull(wg.getWIDGET_ID()) ||
	 * Utility.isEmptyOrNull(wg.getWIDGET_NAME())) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "id or name invalid"); }
	 * 
	 * MySqlConnection con = new MySqlConnection(); int ret =
	 * con.insertInWso2(wg);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "insert widget resource fail"); }
	 * } public Widget_Resource getWidget(String wg_id, String wg_name) { if
	 * (Utility.isEmptyOrNull(wg_id) || Utility.isEmptyOrNull(wg_name)) { return
	 * null; }
	 * 
	 * Widget_Resource wg = new Widget_Resource(); wg.setWIDGET_ID(wg_id);
	 * wg.setWIDGET_NAME(wg_name);
	 * 
	 * MySqlConnection con = new MySqlConnection(); return
	 * con.getInWso2(Widget_Resource.class, wg); } public RetValData
	 * updateWidget(Widget_Resource wg) { if
	 * (Utility.isEmptyOrNull(wg.getWIDGET_ID()) ||
	 * Utility.isEmptyOrNull(wg.getWIDGET_NAME())) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "id or name invalid"); }
	 * 
	 * MySqlConnection con = new MySqlConnection(); Widget_Resource wgtmp = new
	 * Widget_Resource(); wgtmp.setWIDGET_ID(wg.getWIDGET_ID());
	 * wgtmp.setWIDGET_NAME(wg.getWIDGET_NAME());
	 * 
	 * wgtmp = con.getInWso2(Widget_Resource.class, wgtmp); if
	 * (wgtmp.getWIDGET_CONFIGS() == null || wgtmp.getWIDGET_CONFIGS().length()
	 * == 0) return new RetValData(ErrorCode.Object_Not_Existed.code, "",
	 * "widget empty");
	 * 
	 * int ret = con.updateInWso2(Widget_Resource.class, wg);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "update widget resource fail"); }
	 * } public RetValData deleteWidget(String wg_id, String wg_name) { if
	 * (Utility.isEmptyOrNull(wg_id) || Utility.isEmptyOrNull(wg_name)) { return
	 * new RetValData(ErrorCode.Invalid_Input.code, "", "id or name invalid"); }
	 * 
	 * Widget_Resource wg = new Widget_Resource(); wg.setWIDGET_ID(wg_id);
	 * wg.setWIDGET_NAME(wg_name);
	 * 
	 * MySqlConnection con = new MySqlConnection(); int ret =
	 * con.deleteInWso2(Widget_Resource.class, wg);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "delete widget resource fail"); }
	 * }
	 */

	/*
	 * @SuppressWarnings("unused") private final int _____dashboard_____ = 0;
	 * public RetValData addDashBoard_Resource(Dashboard_Resource dashboard) {
	 * if (Utility.isEmptyOrNull(dashboard.getURL()) ||
	 * Utility.isEmptyOrNull(dashboard.getOWNER())) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "url or owner invalid"); }
	 * 
	 * MySqlConnection con = new MySqlConnection(); int ret =
	 * con.insertInWso2(dashboard);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "add dashboard resource fail"); }
	 * } public Dashboard_Resource getDashboard_Resource(String URL, String
	 * OWNER) { if (Utility.isEmptyOrNull(URL) || Utility.isEmptyOrNull(OWNER))
	 * { System.err.println("getDashboard_Resource: invalid url or owner");
	 * return null; }
	 * 
	 * Dashboard_Resource dashboard = new Dashboard_Resource();
	 * dashboard.setOWNER(OWNER); dashboard.setURL(URL);
	 * 
	 * MySqlConnection con = new MySqlConnection();
	 * 
	 * return con.getInWso2(Dashboard_Resource.class, dashboard); } public
	 * RetValData updateDashboard_Resource(Dashboard_Resource dashboard) { if
	 * (Utility.isEmptyOrNull(dashboard.getURL()) ||
	 * Utility.isEmptyOrNull(dashboard.getOWNER())) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "url or owner invalid"); }
	 * 
	 * MySqlConnection con = new MySqlConnection();
	 * 
	 * Dashboard_Resource tmp = new Dashboard_Resource();
	 * tmp.setOWNER(dashboard.getOWNER()); tmp.setURL(dashboard.getURL());
	 * 
	 * tmp = con.getInWso2(Dashboard_Resource.class, tmp); if (tmp == null ||
	 * tmp.getCONTENT() == null || tmp.getCONTENT().length() == 0) return new
	 * RetValData(ErrorCode.Object_Not_Existed.code, "", "object not exist");
	 * 
	 * int ret = con.updateInWso2(Dashboard_Resource.class, dashboard);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "",
	 * "update dashboard resource fail"); } } public RetValData
	 * deleteDashboard_Resource(String URL, String OWNER) { if
	 * (Utility.isEmptyOrNull(URL) || Utility.isEmptyOrNull(OWNER)) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "url or owner invalid"); }
	 * 
	 * Dashboard_Resource dashboard = new Dashboard_Resource();
	 * dashboard.setOWNER(OWNER); dashboard.setURL(URL);
	 * 
	 * MySqlConnection con = new MySqlConnection();
	 * 
	 * int ret = con.deleteInWso2(Dashboard_Resource.class, dashboard); if (ret
	 * == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code); } else {
	 * return new RetValData(ret, "", "delete dashboard resource fail"); }
	 * 
	 * }
	 */

	@SuppressWarnings("unused")
	private final int _____client_dashboard_____ = 0;
	//get
	public List<String> getDashBoardData(String userId, int fieldId, int groupID) {
		System.out.println("Call getDashBoardData");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("fieldId: " + fieldId);
			System.out.println("groupID: " + groupID);
		}
		List<String> rs = new ArrayList<>();
		if (Utility.isEmptyOrNull(userId)) {
			System.out.println("Invalid input data");
			return rs;
		}

		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null) {
			System.out.println("User not existed");
			return rs;
		}
		if (user.getLoginState() != 1) {
			System.out.println("user not login");
			return rs;
		}

		Controller ctr = new Controller();
		rs = ctr.getDashBoardData(user, fieldId, groupID);
		return rs;
	}

	public RetValData downloadConfigFile(String userId) {
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"userID invalid");
		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code, "",
					"user not login");
		// TODO: get config from db
		File confFile = new File("C://config//" + userId + ".conf");
		if (!confFile.exists()) {
			File confAdmin = new File("C://config//admin.conf");
			try {
				FileUtils.copyFile(confAdmin, confFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		byte[] configData = Utility.readFileToByte("C://config//" + userId
				+ ".conf");
		if (configData.length == 0)
			return new RetValData(ErrorCode.Not_OK.code, "",
					"config data empty");
		return new RetValData(Base64Utils.base64Encode(configData));
	}

	public RetValData uploadConfigFile(String userId, String base64ConfigData,
			int fieldId) {
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"user id invalid");
		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code);

		Controller ctr = new Controller();
		int ret = ctr.uploadConfigFile(user, base64ConfigData, fieldId);
		if (ret == ErrorCode.OK.code) {
			return new RetValData(ErrorCode.OK.code);
		} else {
			return new RetValData(ret, "", "upload config fail");
		}
	}

	@SuppressWarnings("unused")
	private final int _____client_chart_____ = 0;
	
	public RetValData getChart(String userId, int chartId) {
		System.out.println("Call getChart");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("chartId: " + chartId);
		}
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"user id invalid");
		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code);

		FormulaChart fml = con.get(FormulaChart.class, chartId);
		if (fml == null)
			return new RetValData(ErrorCode.Not_OK.code, "",
					"FormulaChart null");
		JSONObject obj = new JSONObject();
		try {
			obj.put("chartOption", new JSONObject(fml.getChartoption()));
			obj.put("chartName", fml.getChartname());
			obj.put("jsonParam", new JSONObject(fml.getJsonParam()));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RetValData(ErrorCode.Not_OK.code, "", "JSONException");
		}
		return new RetValData(ErrorCode.OK.code, obj.toString(), "");

	}
	
	public RetValData updateChart(String userId, int chartId, String chartName,
			String option, String jsonParam) {
		System.out.println("Call updateChart");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("chartId: " + chartId);
			System.out.println("chartName: " + chartName);
			System.out.println("option: " + option);
			System.out.println("jsonParam: " + jsonParam);			
		}
		
		
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"user id invalid");
		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code);
		try {
			JSONObject obj = new JSONObject(jsonParam);
			BISQLController ctr = new BISQLController();
			JSONObject objView = new JSONObject();
			objView.put("xAxis", obj.getString("xAxis"));
			objView.put("startedTime", obj.getString("startedTime"));
			objView.put("endedTime", obj.getString("endedTime"));
			RetValData ret = ctr.genChartSQL(objView.toString(),
					obj.getString("data"));
			int sttcId = Integer.valueOf(obj.getString("sttcId"));
			int chartType = Integer.valueOf(obj.getString("chartType"));
			String xaxis = obj.getString("xAxis");
			if (ret.getCode() == 0) {
				String sql = ret.getData();
				if (sql != "") {
					Controller ctrl = new Controller();
					String data = ctrl.renderChartOfFormula(chartName, xaxis,
							sql);
					// chart.setChartoption(chartoption);
					if (data != "") {
						FormulaChart chart = new FormulaChart();
						JSONObject optionsObj = new JSONObject(option);
						optionsObj.put("title", chartName);
						chart.setChartname(chartName);
						chart.setChartoption(optionsObj
								.getJSONObject("options").toString());
						chart.setSql(sql);
						chart.setStatusId(1);
						chart.setSttcId(sttcId);
						switch (optionsObj.getString("chartType")) {
						case "ColumnChart":
							chartType = 1;
							break;

						case "LineChart":
							chartType = 2;
							break;
						case "PieChart":
							chartType = 3;
							break;

						case "ComboChart":
							chartType = 4;
							break;

						case "Table":
							chartType = 5;
							break;

						case "AreaChart":
							chartType = 6;
							break;
						default:
							chartType = 1;
							break;
						}
						chart.setId(chartId);
						chart.setType(chartType);
						chart.setXaxis(xaxis);
						chart.setJsonParam(jsonParam);
						int result = con.update(FormulaChart.class, chart);
						if (result == 0)
							return new RetValData(ErrorCode.OK.code, "",
									"Lưu thành công");
					}
				}

				return new RetValData(ErrorCode.Not_OK.code, "", "sql null ");

			}
			return ret;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("renderChart JSONException");
		}

		return new RetValData(ErrorCode.Not_OK.code);
	}
	
	public RetValData removeChart(String userId, int chartId) {
		System.out.println("Call removeChart");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("chartId: " + chartId);
		}
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"user id invalid");
		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code);

		int result = con.delete(FormulaChart.class, chartId);
		return new RetValData(result, "", "remove chart");

	}

	public RetValData updateStatusChart(String userId, int chartId, int statusId) {
		System.out.println("Call updateStatusChart");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("chartId: " + chartId);
			System.out.println("statusId: " + statusId);
		}
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"user id invalid");
		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code);

		FormulaChart fml = con.get(FormulaChart.class, chartId);
		fml.setStatusId(statusId);
		con.update(FormulaChart.class, fml);
		return new RetValData(ErrorCode.OK.code);

	}

	public RetValData saveChart(String userId, String chartName, String option,
			String jsonObj) {
		System.out.println("Call renderChart");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("jsonObj: " + jsonObj);
		}
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"user id invalid");
		MySqlConnection con = new MySqlConnection();

		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code);
		try {
			JSONObject obj = new JSONObject(jsonObj);
			BISQLController ctr = new BISQLController();
			JSONObject objView = new JSONObject();
			objView.put("xAxis", obj.getString("xAxis"));
			objView.put("startedTime", obj.getString("startedTime"));
			objView.put("endedTime", obj.getString("endedTime"));
			RetValData ret = ctr.genChartSQL(objView.toString(),
					obj.getString("data"));
			int sttcId = Integer.valueOf(obj.getString("sttcId"));
			int chartType = Integer.valueOf(obj.getString("chartType"));
			String xaxis = obj.getString("xAxis");
			if (ret.getCode() == 0) {
				String sql = ret.getData();
				if (sql != "") {
					Controller ctrl = new Controller();
					String data = ctrl.renderChartOfFormula(chartName, xaxis,
							sql);
					// chart.setChartoption(chartoption);
					if (data != "") {
						FormulaChart chart = new FormulaChart();
						JSONObject optionsObj = new JSONObject(option);
						optionsObj.put("title", chartName);
						chart.setChartname(chartName);
						chart.setChartoption(optionsObj
								.getJSONObject("options").toString());
						chart.setSql(sql);
						chart.setStatusId(1);
						chart.setSttcId(sttcId);
						switch (optionsObj.getString("chartType")) {
						case "ColumnChart":
							chartType = 1;
							break;

						case "LineChart":
							chartType = 2;
							break;
						case "PieChart":
							chartType = 3;
							break;

						case "ComboChart":
							chartType = 4;
							break;

						case "Table":
							chartType = 5;
							break;

						case "AreaChart":
							chartType = 6;
							break;
						default:
							chartType = 1;
							break;
						}
						chart.setType(chartType);
						chart.setXaxis(xaxis);
						chart.setJsonParam(jsonObj);
						int result = con.insert(chart);
						if (result == 0)
							return new RetValData(ErrorCode.OK.code, "",
									"Lưu thành công");
					}
				}

				return new RetValData(ErrorCode.Not_OK.code, "", "sql null ");

			}
			return ret;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("renderChart JSONException");
		}

		return new RetValData(ErrorCode.Not_OK.code);

	}

	public RetValData renderChart(String userId, String jsonObj) {
		System.out.println("Call renderChart");
		if (iWriteLog) {
			System.out.println("userId: " + userId);
			System.out.println("jsonObj: " + jsonObj);
		}
		if (Utility.isEmptyOrNull(userId))
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"user id invalid");
		MySqlConnection con = new MySqlConnection();

		User user = con.get(User.class, userId);
		if (user == null)
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user not found");
		if (user.getLoginState() == 0)
			return new RetValData(ErrorCode.User_Not_Login.code);

		try {
			JSONObject obj = new JSONObject(jsonObj);
			BISQLController ctr = new BISQLController();
			JSONObject objView = new JSONObject();
			objView.put("xAxis", obj.getString("xAxis"));
			objView.put("startedTime", obj.getString("startedTime"));
			objView.put("endedTime", obj.getString("endedTime"));
			RetValData ret = ctr.genChartSQL(objView.toString(),
					obj.getString("data"));
			int sttcId = Integer.valueOf(obj.getString("sttcId"));
			int chartType = Integer.valueOf(obj.getString("chartType"));
			// String options =
			// "{\"height\":400, \"legendTextStyle\":{ \"color\":\"#FFF\" }, \"titleTextStyle\":{ \"color\":\"#FFF\" }, \"backgroundColor\":\"#34447D\", \"vAxis\":{ \"textStyle\":{ \"color\":\"#FFF\" }, \"gridlines\":{ \"color\":\"transparent\" }}, \"hAxis\":{ \"textStyle\":{ \"color\":\"#FFF\" },\"title\": \"N\u0103m\",\"titleTextStyle\":{\"color\": \"#FFF\"}}}";
			if (ret.getCode() == 0) {
				String sql = ret.getData();
				System.out.println("sql: " + sql);
				if (sql != "") {
					Controller ctrl = new Controller();
					String data = ctrl.renderChartOfFormula("bieu do test",
							obj.getString("xAxis"), sql);
					// chart.setChartoption(chartoption);
					return new RetValData(ErrorCode.OK.code, data, "");
				} else
					return new RetValData(ErrorCode.Not_OK.code, "",
							"sql null ");

			}
			return ret;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("renderChart JSONException");
		}

		return new RetValData(ErrorCode.Not_OK.code);
	}

	// public RetValData renderComponentChart(){
	//
	// }

	/*
	 * public RetValData getDetailChart(String userId, int groupId, int sttcId)
	 * { JSONObject json = new JSONObject(); MySqlConnection con = new
	 * MySqlConnection(); Group group = con.get(Group.class, groupId); User user
	 * = con.get(User.class, userId); if (group == null) { return new
	 * RetValData(ErrorCode.Object_Not_Existed.code, "", "group null"); } if
	 * (user == null) { return new RetValData(ErrorCode.User_Not_Existed.code,
	 * "", "user null"); }
	 * 
	 * //TODO: xem lai. //logic tu haintn+vietpdb: admin chi thao tac voi chi
	 * tieu do admin tao ra. // con user thuong chi duoc thay doi chi tieu do
	 * user thuong tao ra
	 * 
	 * //=>chi tieu user tao ra thi chi user nhin thay va sua //con chi tieu
	 * admin tao ra thi tat ca deu nhin thay, chi admin duoc sua if
	 * (user.getPermission() == 1 && !group.getUserId().equals("admin")) //
	 * Admin // user { return new
	 * RetValData(ErrorCode.Admin_Cannot_Insert_User_Group.code, "", ""); }
	 * 
	 * Statistical sttc = con.get(Statistical.class, sttcId); if (sttc == null)
	 * { return new RetValData(ErrorCode.Invalid_Input.code, "",
	 * "statistical null"); }
	 * 
	 * if (sttc.getGroupId() != group.getGroupId()) { return new
	 * RetValData(ErrorCode.Object_Not_Existed.code, "", "group null"); }
	 * 
	 * //String url = con.getURLDashboardBySttcId(sttcId); ChartObject chart =
	 * con.getChartObjectBySttcId(sttcId); //if(url.length() == 0) url = "mic";
	 * if (chart != null) { try { json.put("chartType", chart.getChartType());
	 * json.put("chartName", chart.getChartName()); json.put("startedDate",
	 * chart.getStartedDate()); json.put("endDate", chart.getEndDate());
	 * json.put("typeTime", chart.getTypeTime()); json.put("chartColumnList",
	 * chart.getChartColumnList()); json.put("url",chart.getUrlWebView()); }
	 * catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * return new RetValData(json.toString()); } return new
	 * RetValData(ErrorCode.Object_Not_Existed.code, "", "end of function"); }
	 */

	/*
	 * @SuppressWarnings("unused") private final int
	 * _____client_make_formula_and_chart_____ = 0; public BIColumn
	 * getBIColumn(int Id) { MySqlConnection con = new MySqlConnection();
	 * BIColumn bi = con.get(BIColumn.class, Id); return bi; } public
	 * List<String> getBIColumnList(String userId, int groupId, int period, int
	 * areaType) { MySqlConnection con = new MySqlConnection(); User user =
	 * con.get(User.class, userId); if (user == null || user.getLoginState() !=
	 * 1) return new ArrayList<String>(); Group group = con.get(Group.class,
	 * groupId); if (group == null) return new ArrayList<String>(); if
	 * (!group.getUserId().equals(userId)) return new ArrayList<String>();
	 * Controller ctr = new Controller(); return
	 * ctr.getDescriptionOfBIColumn(groupId, period, areaType); } public
	 * List<String> getBIColumnListByIdList(String listId) { if
	 * (Utility.isEmptyOrNull(listId)) return new ArrayList<String>(); String[]
	 * list = listId.split("\\.");
	 * 
	 * if (list.length == 0) list[0] = listId; MySqlConnection con = new
	 * MySqlConnection(); List<String> rs = new ArrayList<String>(); for (String
	 * s : list) { BIColumn col = con.get(BIColumn.class, Integer.parseInt(s));
	 * 
	 * JSONObject json = new JSONObject();
	 * 
	 * try { json.put("description", col.getDescription()); json.put("column",
	 * col.getColumnName()); json.put("ColumnId", col.getID());
	 * json.put("TableId", col.getTableID()); } catch (JSONException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * rs.add(json.toString()); }
	 * 
	 * return rs; }
	 * 
	 * public RetValData addChartObject(ChartObject chart) { MySqlConnection con
	 * = new MySqlConnection(); SimpleDateFormat formatter = new
	 * SimpleDateFormat("dd/MM/yyyy"); formatter.setLenient(false); try {
	 * formatter.parse(chart.getEndDate().trim()); } catch (Exception e) {
	 * chart.setEndDate(formatter.format(new Date())); }
	 * 
	 * try { formatter.parse(chart.getStartedDate().trim()); } catch (Exception
	 * e) { Calendar calendar = Calendar.getInstance(); calendar.set(2010, 1,
	 * 1); chart.setEndDate(formatter.format(calendar.getTime())); }
	 * 
	 * Statistical sttc = con.get(Statistical.class, chart.getSttcId()); if
	 * (sttc == null) return new RetValData(ErrorCode.Object_Not_Existed.code,
	 * "", "statistical null");
	 * 
	 * ChartObject chartdb = con.getChartObjectBySttcId(chart.getSttcId());
	 * 
	 * if (chartdb != null) { chart.setId(chartdb.getId()); if
	 * (con.update(ChartObject.class, chart) == 0) return new
	 * RetValData(ErrorCode.OK.code, Integer.toString(chartdb.getId()),
	 * "updated"); }
	 * 
	 * if (con.insert(chart) == 0) return new RetValData(ErrorCode.OK.code,
	 * Integer.toString(chart.getId()), "inserted"); else return new
	 * RetValData(ErrorCode.Not_OK.code, "", "insert fail"); }
	 * 
	 * //client day dieu kien hoac object de tao thanh cau sql //server chua xu
	 * ly xong, dang mac dinh tra ve ok public RetValData
	 * inputAttributesSql(String jsonObject) { if
	 * (Utility.isEmptyOrNull(jsonObject)) return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "jsonObject null or empty");
	 * System.out.println(jsonObject);
	 * 
	 * String advanceCondition = ""; MySqlConnection con = new
	 * MySqlConnection(); try { JSONArray jsonArr = new JSONArray(jsonObject);
	 * for(int i = 0 ; i < jsonArr.length() ; i++) { JSONObject object =
	 * jsonArr.getJSONObject(i); String type = object.getString("type"); String
	 * value = object.getString("value");
	 * 
	 * if(type.trim().equals("2")) { advanceCondition = advanceCondition + " " +
	 * value; } else if(type.trim().equals("1")) { int columnId =
	 * Integer.parseInt(value); BIColumn column = con.get(BIColumn.class,
	 * columnId); advanceCondition = advanceCondition + " " +
	 * column.getColumnName(); } else { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "", "type invalid"); } }
	 * 
	 * 
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return new RetValData(ErrorCode.Invalid_Input.code,
	 * "", "jsonObject not in json format"); }
	 * 
	 * return new RetValData(ErrorCode.OK.code); }
	 * 
	 * //co the day la nut luu lai public RetValData inputAdvancedFormula(String
	 * jsonObject, String name) { if (Utility.isEmptyOrNull(jsonObject)) return
	 * new RetValData(ErrorCode.Invalid_Input.code, "",
	 * "jsonObject null or empty"); System.out.println(jsonObject); Controller
	 * ctr = new Controller(); String sql =
	 * ctr.getSQLFromJSONObject(jsonObject); return new
	 * RetValData(ErrorCode.OK.code); }
	 * 
	 * //check cac doi tuong gui len co the tao thanh cau sql dung hay khong
	 * public RetValData checkAdvancedFormula(String jsonObject, String name) {
	 * if (Utility.isEmptyOrNull(jsonObject)) return new
	 * RetValData(ErrorCode.Invalid_Input.code); System.out.println(jsonObject);
	 * 
	 * return new RetValData(ErrorCode.OK.code); }
	 */

	public List<String> getListOfCity() {
		MySqlConnection con = new MySqlConnection();
		return con.getListOfCity();
	}

	// public List<String> getListDistrictOfCity(int cityId) {
	// if (cityId < 0)
	// return new ArrayList<String>();
	// if (cityId == 2) {
	// JSONObject json = new JSONObject();
	// try {
	// json.put("regional_id", cityId);
	// json.put("name", "Tất cả");
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// List<String> list = new ArrayList<String>();
	// list.add(json.toString());
	// return list;
	// }
	// MySqlConnection con = new MySqlConnection();
	// return con.getListDistrictOfCity(cityId);
	// }

	// ipad client da goi len khi bam luu lai trong phan nang cao.
	// server chua xu ly
	// public RetValData addAdvancedFormula(AdvancedFormula af) {
	// if (Utility.isEmptyOrNull(af.getAdFormulaName())
	// || af.getGroupId() <= 0) {
	// return new RetValData(ErrorCode.Invalid_Input.code, "",
	// "name or id NULL");
	// }
	//
	// MySqlConnection con = new MySqlConnection();
	//
	// Group group = con.get(Group.class, af.getGroupId());
	// if (group == null)
	// return new RetValData(ErrorCode.Object_Not_Existed.code, "",
	// "group not exist");
	//
	// int ret = con.insert(af);
	// if (ret == ErrorCode.OK.code) {
	// return new RetValData(ErrorCode.OK.code);
	// } else {
	// return new RetValData(ret, "", "insert advance formula fail");
	// }
	// }

	// public RetValData addChartJson(String chartJson) {
	// // {
	// // "chartType": 1,
	// // "sttcId": 25,
	// // "date": {
	// // "startedDate": "01/02/2018",
	// // "endDate": "01/12/2018"
	// // },
	// // "typeTime": 1,
	// // "listDescription": [
	// // {
	// // "id": 23,
	// // "name": "Doanh thu 3G_Viettel",
	// // "type": [
	// // {
	// // "type": 1,
	// // "value": 1
	// // },
	// // {
	// // "type": 2,
	// // "value": 201
	// // }
	// // ]
	// // },
	// // {
	// // "id": 23,
	// // "name": "Doanh thu 3G_Vinaphone",
	// // "type": [
	// // {
	// // "type": 1,
	// // "value": 1
	// // },
	// // {
	// // "type": 2,
	// // "value": 201
	// // }
	// // ]
	// // },
	// // {
	// // "id": 23,
	// // "name": "Doanh thu 3G_Mobifone",
	// // "type": [
	// // {
	// // "type": 1,
	// // "value": 1
	// // },
	// // {
	// // "type": 2,
	// // "value": 201
	// // }
	// // ]
	// // }
	// // ],
	// // "urlWebView": "https://mic.bkav.com:9643/portal/dashboard/mic"
	// // }
	//
	//
	// //add widget
	// MySqlConnection con = new MySqlConnection();
	// JSONObject param = null;
	//
	// try {
	// param = new JSONObject(chartJson);
	// BISQLController biCtr = new BISQLController();
	// Controller ctr = new Controller();
	// ChartForm chartForm = new ChartForm();
	//
	// chartForm.setChartType(Integer.parseInt(param.get("chartType").toString()));
	// chartForm.setSttcId(Integer.parseInt(param.get("sttcId").toString()));
	// chartForm.setTypeTime(Integer.parseInt(param.get("typeTime").toString()));
	// chartForm.setUrlWebView(param.get("urlWebView").toString());
	// JSONObject date = new JSONObject(param.getString("date"));
	// TimeTool startedDate = new TimeTool(param.get("startedDate").toString());
	// TimeTool endedDate = new TimeTool(param.get("endedDate").toString());
	//
	// switch (chartForm.getTypeTime()){
	// case 1://day
	// chartForm.setStartedDate(startedDate.getNgay());
	// chartForm.setEndedDate(endedDate.getNgay());
	// break;
	// case 2://week
	// //now not support
	// chartForm.setStartedDate(startedDate.getTuan());
	// chartForm.setEndedDate(endedDate.getTuan());
	// break;
	// case 3://month
	// chartForm.setStartedDate(startedDate.getThang());
	// chartForm.setEndedDate(endedDate.getThang());
	// break;
	// case 4://quarter
	// chartForm.setStartedDate(startedDate.getQuy());
	// chartForm.setEndedDate(endedDate.getQuy());
	// break;
	// case 5://year
	// chartForm.setStartedDate(startedDate.getNam());
	// chartForm.setEndedDate(endedDate.getNam());
	// break;
	// default:
	// return new
	// RetValData(ErrorCode.Invalid_Input.code,"","type time invalid");
	// }
	//
	//
	// long id = con.countRecordOfTableInWSO2(Widget_Resource.class) + 1;
	// Statistical sttc = null;
	//
	// sttc = con.get(Statistical.class,
	// Integer.parseInt(param.get("sttcId").toString()));
	//
	// String widgetId =
	// Long.toString(id)+"_"+Utility.convertStringSignToNoSign(sttc.getName());
	//
	// List<ChartPresentObject> chartPresentObjectList =
	// ctr.convertJsonArrayToChartPresentObjectList(param.getJSONArray("listDescription"));
	//
	// if (chartPresentObjectList.size() == 0){
	// System.out.println("list description empty");
	// return new RetValData(ErrorCode.Invalid_Input.code, "",
	// "list description empty");
	// }
	//
	// //check chart present value
	// boolean isPresentSameColumm = true;
	// for (int i = 1; i < chartPresentObjectList.size(); i ++){
	// if (chartPresentObjectList.get(i).getId() !=
	// chartPresentObjectList.get(0).getId()){
	// isPresentSameColumm = false;
	// }
	// }
	//
	// Set<String> biColumnNameSet = new HashSet<String>(); //distinct
	// Set<BIColumn> biColumnSet = new HashSet<BIColumn>(); //distinct
	// Set<Integer> biTableIdSet = new HashSet<Integer>(); //distinct
	// Set<String> biTableNameSet = new HashSet<String>(); //distinct
	//
	// //get column id and table id
	// for (ChartPresentObject chartPresentObject:chartPresentObjectList){
	// BIColumn bicolumn = con.get(BIColumn.class, chartPresentObject.getId());
	// biColumnSet.add(bicolumn);
	// biColumnNameSet.add(bicolumn.getColumnName());
	// biTableIdSet.add(bicolumn.getTableID());
	// }
	// //get table name
	// for (Integer tableId:biTableIdSet){
	// biTableNameSet.add(con.get(BITable.class, tableId).getName());
	// }
	// //TODO: complete
	// if (isPresentSameColumm){
	// String presentValue = biColumnNameSet.iterator().next();
	// //biCtr.genQueryFrom(biTableNameSet, presentValue,
	// chartForm.getChartType(), chartForm.getStartedDate(),
	// chartForm.getEndedDate(), );
	// } else {
	//
	// }
	//
	// //RetValData retSqlWidget = biCtr.genQueryFrom(listTable, fml,
	// sttc.getTypeTime(), listBusiness);
	// List <String> columnName = new ArrayList<String>();
	// //TODO: get from json
	//
	// RetValData retSqlWidget = new
	// RetValData("select nam, tonglaodong_nam from laodong");
	//
	// System.out.println("sql: " + retSqlWidget.getData());
	// if(con.createViewsTable(widgetId, retSqlWidget.getData()) != 0)
	// {
	// return new RetValData(ErrorCode.Create_View_Table_Fail.code, "",
	// "createViewsTable fail");
	// }
	//
	// int checkAddWidget = ctr.addWidget_Resource(Define.Bar_Chart.code,
	// widgetId,
	// "select * from " + widgetId, sttc.getTypeTime(),
	// widgetId, columnName, sttc.getId());
	//
	// if(checkAddWidget != ErrorCode.OK.code) return new
	// RetValData(ErrorCode.Not_OK.code, "", "addWidget Resource fail");
	//
	//
	//
	// //TODO: update dashboard with widget list id
	// //updatedashboardresource -> done
	// /*
	// * to do :
	// * if delete table ChartObject ->
	// * check again getDetailChart ( Screen Sttcs detail ) -> data result
	// client
	// *
	// * need create a table save defaults sttcs with typeTime create sttcs
	// * */
	// int iCheckUpdateDashboard = ctr.updateDashboardResource(sttc.getId(),
	// "admin");
	// if(iCheckUpdateDashboard != ErrorCode.OK.code) {
	// return new RetValData(ErrorCode.Not_OK.code, "",
	// "update dashboard resource fail");
	// }
	//
	//
	// // int checkAddDashboard = ctr.addDashboard_Resource(widgetId,
	// sttc.getUserId(), widgetId, sttc.getId());
	// // if(ch eckAddDashboard != ErrorCode.OK.code) return
	// ErrorCode.Not_OK.code;
	// //end addwidget
	//
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return new RetValData(ErrorCode.INVALID_PARAM.code, "",
	// "json input not in json format");
	// }
	// return new RetValData(ErrorCode.OK.code);
	// }

	public String executeQuery(String userId, String sql) {
		System.out.println("sql: " + sql);
		MySqlConnection con = new MySqlConnection();
		return con.executeNativeSQL(sql);
	}

	@SuppressWarnings("unused")
	private final int _____user_____ = 0;

	/*
	 * public RetValData addUser(User user) { if
	 * (Utility.isEmptyOrNull(user.getUserId()) ||
	 * Utility.isEmptyOrNull(user.getUserName()) ||
	 * Utility.isEmptyOrNull(user.getPasswordDigest())) { return new
	 * RetValData(ErrorCode.Invalid_Input.code, "",
	 * "userID or userName or userPasswordDigest NULL"); }
	 * 
	 * MySqlConnection con = new MySqlConnection(); int ret = con.insert(user);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "insert user fail"); } }
	 */
	/*
	 * public User getUser(String userId) { MySqlConnection con = new
	 * MySqlConnection(); return con.get(User.class, userId); } public
	 * RetValData updateUser(User user) { if
	 * (Utility.isEmptyOrNull(user.getUserId()) ||
	 * Utility.isEmptyOrNull(user.getUserName()) ||
	 * Utility.isEmptyOrNull(user.getPasswordDigest())) { return new
	 * RetValData(ErrorCode
	 * .Invalid_Input.code,"","id or username or password fail"); }
	 * MySqlConnection con = new MySqlConnection(); int ret =
	 * con.update(User.class, user);
	 * 
	 * if (ret == ErrorCode.OK.code){ return new RetValData(ErrorCode.OK.code);
	 * } else { return new RetValData(ret, "", "update user fail"); } }
	 */
	public RetValData login(String userId, String password) {
		if (Utility.isEmptyOrNull(userId) || Utility.isEmptyOrNull(password)) {
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"userId or password invalid");
		}

		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null) {
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user null");
		}

		if (password.trim().equals(user.getPasswordDigest())) {
			user.setLoginState(1);
			con.update(User.class, user);
			return new RetValData(ErrorCode.OK.code, "", "update success");
		}
		return new RetValData(ErrorCode.User_Pass_Wrong.code);
	}

	public RetValData logout(String userId) {
		if (Utility.isEmptyOrNull(userId)) {
			return new RetValData(ErrorCode.Invalid_Input.code, "",
					"userId empty");
		}

		MySqlConnection con = new MySqlConnection();
		User user = con.get(User.class, userId);
		if (user == null) {
			return new RetValData(ErrorCode.User_Not_Existed.code, "",
					"user null");
		}

		if (user.getLoginState() == 1) {
			user.setLoginState(0);
			con.update(User.class, user);
			return new RetValData(ErrorCode.OK.code, "", "update user success");
		}
		return new RetValData(ErrorCode.User_Not_Login.code, "",
				"user not login");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// System.out.println(addMisstionOfSttc("dungdt",
		// "{ \"sttcId\": 149, \"userMonitor\": \"admin\", \"userExecute\": \"cuchaiquan\", \"missionContent\": \"Yêu cầu đáasdasasd\", \"documentDate\": \"19/08/2019\", \"file\": \"\" }"));
		// System.out.println(getMissonOfSttc("dungdt",0));
		// System.out.println(getListGroupByUser("admin"));
		// System.out.println(getGroupDetailByUser("admin"));
		// System.out.println(getDocumentOfUser("admin"));
		// System.out.println(getListStatisticalOfGroup("admin", 1));
		// System.out.println(getMissonOfSttc("dungdt", 96));
		// System.out.println(getDatafromFormula(""));
		// System.out.println(addStatistical("admin",
		// "{ \"groupId\": 1, \"sttcName\": \"T\u0103ng tr\u01B0\u1EDFng a\", \"realityValue\": 2000, \"unitOfRealityValue\": \"%\", \"kpiValue\": 3000, \"timeType\": 1, \"unitOfGrowthValue\": \"%\"}"));
		// System.out.println(getListSttcPagingForUser("admin", 1,3));
		BIMICService bi = new BIMICService();
		System.out.println(bi.getFieldOfUser("ntnam"));
	}

}
