//
//  SideMenu.swift
//  AppEAH
//
//  Created by Marius Voxbrunner on 04.07.23.
//

import SwiftUI

struct SideMenu: View {
    @Binding var selectedTab : String
    @Namespace var animation
    var body: some View {
        VStack(alignment: .leading, spacing: 15, content: {
            Image("EAHLogo")
                .resizable()
                .frame(width: 262, height: 125)
                .padding(.top,50)
            VStack(alignment: .leading,spacing: 6, content: {
                Text("Ernst-Abbe-\nHochschule")
                    .font(.title)
                    .fontWeight(.heavy)
                    .foregroundColor(.white)
             
            })
            VStack(alignment: .leading,spacing:0){
                TabButton(image:"list.dash", title: "Home", animation: animation, selectedTab: $selectedTab)
                TabButton(image: "calendar", title: "Stundenplan", animation: animation, selectedTab: $selectedTab)
                TabButton(image: "fork.knife", title: "Mensaplan", animation: animation, selectedTab: $selectedTab)
                TabButton(image: "", title: "Thoska", animation: animation, selectedTab: $selectedTab)
                TabButton(image: "", title: "Wetter", animation: animation, selectedTab: $selectedTab)
                TabButton(image: "", title: "Rechtliches", animation: animation, selectedTab: $selectedTab)
            }
            .padding(.leading,-15)
           
        })
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct SideMenu_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }
}
