import localFont from "next/font/local";
import "./globals.css";
import StoreProvider from "@/src/redux/StoreProvider";
import Header from "@/src/components/header/Header";
import { Layout } from "antd";

const geistSans = localFont({
  src: "./fonts/GeistVF.woff",
  variable: "--font-geist-sans",
  weight: "100 900",
});
const geistMono = localFont({
  src: "./fonts/GeistMonoVF.woff",
  variable: "--font-geist-mono",
  weight: "100 900",
});

export const metadata = {
  title: "HomyTrack ",
  description:
    "A smart platform for seamless property rental management – contracts, payments, and maintenance in one place.",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased relative`}
      >
        <StoreProvider>
          <Layout className="min-h-screen">
            <Header />
            {children}
          </Layout>
        </StoreProvider>
      </body>
    </html>
  );
}
