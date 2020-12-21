package com.ghc.cn.junit;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ghc.cn.mapper.SysPermissionCustomMapper;
import com.ghc.cn.pojo.SysPermission;
import com.ghc.cn.pojo.TreeMenu;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/applicationContext.xml"})
public class TestMenu {

	@Autowired
	private SysPermissionCustomMapper sysPermissionCustomMapper	;
	
	@Test
	public void testMenu(){
		List<TreeMenu> treeMenus = sysPermissionCustomMapper.findMenuList();
		for (TreeMenu treeMenu : treeMenus) {
			System.out.println(treeMenu.getId()+","+treeMenu.getName());
			
			List<SysPermission> children = treeMenu.getChildren();
			for (SysPermission subMenu : children) {
				System.out.println("\t"+subMenu.getName()+","+subMenu.getUrl()+","+subMenu.getPercode());
			}
		}
	}
}
