"use client";
import DashBoard from "@/src/components/dashBoards/DashBoard";
import {
  AppstoreOutlined,
  ContainerOutlined,
  DesktopOutlined,
  MailOutlined,
  PieChartOutlined,
  SettingOutlined,
} from "@ant-design/icons";
import { Breadcrumb, Layout, Menu } from "antd";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import Rooms from "./Rooms/Rooms";
import Building from "./Building/Building";
import { getListBuilding } from "@/src/redux/slices/buildingSlice";
import { getListRooms, getListRoomsByRole } from "@/src/redux/slices/roomSlice";
import Contract from "./Contract/Contract";
import Staffs from "./Staff/Staffs";
import { getListCustomer, getListStaff } from "@/src/redux/slices/userSlice";
import { getListContracts } from "@/src/redux/slices/contractSlice";
import Customer from "./Customer/Customer";
import DashBoardStaff from "@/src/components/dashBoards/DashBoardStaff";

export default function DashboardScreen() {
  const { user } = useSelector((state) => state.auth);
  const [collapse, setCollapse] = useState(false);
  const [tab, setTab] = useState("1");
  const { Sider, Content } = Layout;
  const dispatch = useDispatch();
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  useEffect(() => {
    dispatch(getListBuilding());
    dispatch(getListRoomsByRole());
    dispatch(getListStaff());
    dispatch(getListCustomer());
    dispatch(getListContracts());
  }, []);
  const items = [
    { key: "1", icon: <PieChartOutlined />, label: "Dashboard" },
    { key: "2", icon: <DesktopOutlined />, label: "Customers" },
    { key: "3", icon: <DesktopOutlined />, label: "Rooms" },
    ...(user?.role == "LANDLORD"
      ? [{ key: "4", icon: <ContainerOutlined />, label: "Building" }]
      : []),
    ...(user?.role == "LANDLORD"
      ? [{ key: "5", icon: <ContainerOutlined />, label: "Staffs" }]
      : []),
    { key: "6", icon: <ContainerOutlined />, label: "Contracts" },
  ];
  const onClick = (e) => {
    console.log("click ", e);
    setTab(e.key);
  };
  const renderScreen = () => {
    switch (tab) {
      case "1":
        return user?.role == "LANDLORD" ? <DashBoard /> : <DashBoardStaff />;
      case "2":
        return <Customer />;
      case "3":
        return <Rooms />;
      case "4":
        return <Building />;
      case "5":
        return <Staffs />;
      case "6":
        return <Contract />;
      default:
        return ;
    }
  };
  return (
    <Layout>
      <Sider>
        <Menu
          onClick={onClick}
          mode="inline"
          defaultSelectedKeys={["1"]}
          style={{ height: "100%", borderRight: 0, padding: "16px" }}
          items={items}
          inlineCollapsed={collapse}
        />
      </Sider>
      <Layout style={{ padding: "0 24px 24px" }}>
        <Content className="bg-white rounded-lg p-4">{renderScreen()}</Content>
      </Layout>
    </Layout>
  );
}
