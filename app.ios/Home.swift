//
//  Home.swift
//  AppEAH
//
//  Created by Marius Voxbrunner on 04.07.23.
//


import SwiftUI
import UIKit
import WebKit
import Foundation


struct Home: View {
    @Binding var selectedTab: String
 //   @State private var isFullScreen = false
    init(selectedTab: Binding<String>) {
        self._selectedTab = selectedTab
        UITabBar.appearance().isHidden = true
    }
    var body: some View {
        
            TabView(selection: $selectedTab){
                HomePage()
                    .tag("Home")
                Stundenplan()
                    .tag("Stundenplan")
                Mensaplan()
                    .tag("Mensaplan")
                Thoska()
                    .tag("Thoska")
                Wetter()
                    .tag("Wetter")
                Rechtliches()
                    .tag("Rechtliches")
    }
     }
    }
struct Home_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }
}
struct HomePage: View{
    var body: some View{
        NavigationView {
                GeometryReader { dimensions in
                    VStack {
                        NewsView(urlString: "https://www.eah-jena.de/hochschule/nachrichten#c19189")
                    }
                    .frame(width: dimensions.size.width, height: dimensions.size.height)
                }
                .navigationTitle("Home")
                .navigationBarTitleDisplayMode(.inline)
            }
        }
    }
    
    struct NewsView: UIViewRepresentable {
        let urlString: String
        
        func makeUIView(context: Context) -> WKWebView {
            let webConfiguration = WKWebViewConfiguration()
            let webView = WKWebView(frame: .zero, configuration: webConfiguration)
            return webView
        }
        
        func updateUIView(_ uiView: WKWebView, context: Context) {
            if let url = URL(string: urlString) {
                let request = URLRequest(url: url)
                uiView.load(request)
            }
        }
    }
struct ResponseData: Codable {
    let studentset: [String: StudentsetData]
}

struct StudentsetData: Codable {
    let stgNameshort: String
    let stgNamelong: String
    let stgDegree: String
    let semesterData: [String: SemesterData]
}

struct SemesterData: Codable {
    let posId: String
    let posNumber: String
    let posName: String
    let studentsetData: [String: StudentData]
}

struct StudentData: Codable {
    let studentsetId: String
    let studentsetName: String
    let studentsetNumber: String
}

struct Stundenplan: View{
    
    @State private var studentsetNames: [String] = []
    
    var body: some View {
        NavigationView{          List(studentsetNames, id: \.self) { studentsetName in
            Text(studentsetName)
        }
        .navigationTitle("Stundenplan")
        .onAppear {
            fetchStudentsetData()
        }
    }
}

func fetchStudentsetData() {
    let urlString = "https://stundenplanung.eah-jena.de/api/mobileapp/v1/studentset/list"
    guard let url = URL(string: urlString) else {
        print("URL not available")
        return
    }
    
    let task = URLSession.shared.dataTask(with: url) { (data, response, error) in
        if let error = error {
            print("Error: \(error)")
            return
        }
        
        if let data = data {
            decodeJSON(jsonData: data)
        }
    }
    
    task.resume()
}

func decodeJSON(jsonData: Data) {
    do {
        let responseData = try JSONDecoder().decode(ResponseData.self, from: jsonData)
        
        var studentsetNames: [String] = []
        for (_, studentsetData) in responseData.studentset {
            studentsetNames.append(studentsetData.stgNamelong)
        }
        
        DispatchQueue.main.async {
            self.studentsetNames = studentsetNames
        }
    } catch {
        print("Error decoding JSON: \(error)")
    }
}
}
struct Thoska: View{
       var body: some View {
        NavigationView{
            VStack{
                Group {
                    // Das ist der graue Bereich unten
                    Rectangle()
                        .frame(width:326.8, height: 205.2)
                        .cornerRadius(12)
                        .foregroundColor(Color.gray)
                    
                        .overlay(
                            Rectangle ()
                                .frame(width:326.8, height: 136.8)
                                .foregroundColor(Color.white)
                            
                                .alignmentGuide(.top, computeValue: { dimension in
                                    dimension[.top]
                                })
                                .offset(y: -34.2)
                            
                        )
                        .overlay(
                            // Das ist der orangene Streifen rechts
                            HStack(alignment: .bottom){
                                
                                Rectangle()
                                // Ecken links abrunden
                                
                                    .foregroundColor(Color.orange)
                                    .frame(width: 40, height: 125)
                                    .overlay(
                                        Text("thoska")
                                            .rotationEffect(.degrees(270))
                                            .foregroundColor(.white))
                                    .italic()
                                    .font(.system(size: 12))
                                
                                    .alignmentGuide(.top, computeValue: { dimension in
                                        dimension[.top]
                                    })
                                    .offset(y: -34.2)
                                    .overlay(
                                        
                                        Image("logoeah")
                                            .resizable()
                                            .offset(x:90)
                                            .offset(y:-75)
                                            .frame(width: 106.9, height: 30)
                                        
                                        
                                    )
                                
                                    .overlay(
                                        Text("Studierendenausweis")
                                            .font(.system(size:12))
                                            .offset(x:230)
                                            .padding(-75)
                                            .bold()
                                        
                                        
                                    )
                                
                                
                                Spacer()
                                
                            }
                            
                        )
                        .offset(y:80)
                }
            }
        }
        .navigationTitle("Thoska")
        .navigationBarTitleDisplayMode(.inline)
    }
}
    struct Mensaplan: View{
        
        var body: some View {
            NavigationView{
                
                Text("Gerichte")
            }
        }
    }

struct WeatherData: Codable, Hashable {
    let temperature: String
    let windSpeed: String
    let windDirection: String
    let chill: String
    let provider: String
    let code: String
    let backgroundId: Int
    let iconId: Int
}

struct Wetter: View {
    @State private var weatherData: [WeatherData] = []
    
    var body: some View {
        NavigationView {
            List(weatherData, id: \.self) { data in
                Text(data.temperature)
                Text(data.windSpeed)
                Text(data.windDirection)
                
            }
            .navigationTitle("Wetter in Jena")
            .onAppear {
                fetchWeatherData()
            }
        }
    }
    
    func fetchWeatherData() {
        let urlString = "http://app.fh-erfurt.de:8080/fheapp/api/eah/weather"
        guard let url = URL(string: urlString) else {
            print("URL not available")
            return
        }
        
        let task = URLSession.shared.dataTask(with: url) { (data, response, error) in
            if let error = error {
                print("Error: \(error)")
                return
            }
            
            if let data = data {
                decodeWJSON(jsonData: data)
            }
        }
        
        task.resume()
    }
    
    func decodeWJSON(jsonData: Data) {
        do {
            let responseData = try JSONDecoder().decode(WeatherData.self, from: jsonData)
            
            DispatchQueue.main.async {
                self.weatherData = [responseData]
            }
        } catch {
            print("Error decoding JSON: \(error)")
        }
    }

}

    struct Rechtliches: View{
        var body: some View{
            NavigationView {
                GeometryReader { dimensions in
                    VStack {
                        NewsView(urlString: "https://www.eah-jena.de/impressum#c4092")
                    }
                    .frame(width: dimensions.size.width, height: dimensions.size.height)
                }
                .navigationTitle("Home")
                .navigationBarTitleDisplayMode(.inline)
            }
        }
    }
    
    struct ViewHeightKey: PreferenceKey {
        static var defaultValue: CGFloat = 0
        
        static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
            value = nextValue()
        }
    }

