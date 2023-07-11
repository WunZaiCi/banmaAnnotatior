package com.banma.BF.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banma.BF.dao.BanKuaiDao;
import com.banma.BF.dao.HuifuDao;
import com.banma.BF.dao.TieZiDao;
import com.banma.BF.entity.BanKuai;
import com.banma.BF.entity.HuiFu;
import com.banma.BF.entity.TieZi;
import com.banma.BF.entity.User;

/**
 * Servlet implementation class HuitieSevlet
 */
@WebServlet({ "/HuitieSevlet", "/huitie" })
public class HuitieSevlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TieZiDao tieZiDao = new TieZiDao();
	private BanKuaiDao banKuaiDao = new BanKuaiDao();
	private HuifuDao huifuDao = new HuifuDao();
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");

		String tidstr = request.getParameter("tid");
		if (tidstr == null || tidstr.trim().isEmpty()) {
			// 错误提示
			request.setAttribute("msg", "没有找到您想要的帖子信息<a href=\"index\">>>>>去首页</a>");
			request.getRequestDispatcher("WEB-INF/listError.jsp").forward(request, response);
			return;
		}
		int tid = Integer.valueOf(tidstr);

		// 判断是否为登陆状态
		User loginUser = (User) request.getSession().getAttribute("user");
		if (loginUser == null) {
			// 错误提示
			request.setAttribute("msg", "请先登录<a href=\"login\">>>>>去登陆</a>");
			request.getRequestDispatcher("WEB-INF/error.jsp").forward(request, response);
			return;
		}

		TieZi tieZi =  tieZiDao.getTieziByTid(tid);
		// 帖子所属板块信息
		BanKuai bk = banKuaiDao.getBanKuaiByBid(tieZi.getBid()); 
		request.setAttribute("bk", bk);
		request.setAttribute("tiezi", tieZi);
		request.getRequestDispatcher("WEB-INF/huifuTiezi.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		
		String tidstr = request.getParameter("tid");
		if(tidstr==null||tidstr.trim().isEmpty()) {
			//错误提示
			request.getRequestDispatcher("WEB-INF/listError.jsp").forward(request, response);
			return;
		}
		
		int tid = Integer.valueOf(tidstr);
		String content = request.getParameter("content");
		
		TieZi tieZi = tieZiDao.getTieziByTid(tid);
		if(tieZi==null) {
			//错误提示
			request.setAttribute("msg", "没有找到您想要的帖子信息<a href=\"index\" >>>>>去首页</a>");
			request.getRequestDispatcher("WEB-INF/error.jsp").forward(request, response);
			return;
		}
		
		if(content==null||content.trim().isEmpty()) {
			//错误提示
			request.setAttribute("msg", "标题或内容不能为空<a href=\"updateTiezi?tid="+tid+"\" >>>>>去修改</a>");
			request.getRequestDispatcher("WEB-INF/error.jsp").forward(request, response);
			return;
		}
		
		User loginUser =  (User) request.getSession().getAttribute("user");
		if(loginUser==null) {
			//错误提示
			request.setAttribute("msg", "发帖之前您需要先登录<a href=\"login\">>>>>去登陆</a>");
			request.getRequestDispatcher("WEB-INF/error.jsp").forward(request, response);
		}
		
		
		/**
		 * 把表单中的数据更新到huifu对象中
		 */
		HuiFu huifu = new HuiFu();
		huifu.setUid(loginUser.getUid());
		huifu.setTid(tid);
		huifu.setContent(content);
		 
		
		if(huifuDao.addHuifu(huifu)>0) {//如果回复帖子成功
			response.sendRedirect("tiezi?tid="+tid);
		} else {
			//错误提示
			request.setAttribute("msg", "回复失败，请重试<a href=\"tiezi?tid="+tid+"\">>>>>去帖子详情页</a>");
			request.getRequestDispatcher("WEB-INF/error.jsp").forward(request, response);
			return;
		}
	}
}

