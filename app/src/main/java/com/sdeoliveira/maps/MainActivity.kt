package com.sdeoliveira.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap //lateinit: rastrear a var "map" do tipo "GoogleMap" e depois reinicia-la

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    // A funçao será chamada quando o mapa carregar
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap // Carregar mapa
        createMarker() // Criar marcação
        map.setOnMyLocationButtonClickListener(this) // Criar botão Localizar
        map.setOnMyLocationClickListener(this) // Mostrar latitude e longitude no mapa
        enableLocation() // Ativar localização
    }

    private fun createMarker() {
        val coordenadas = LatLng(1.353291, 103.806107) // Posição em Coordenadas latitude e longitude qualquer
        val marker: MarkerOptions = MarkerOptions().position(coordenadas).title("Reservoir Park - Singapura") // Marca: val coordenadas e nomeia o lugar com: .title
        map.addMarker(marker) // add no map a val marker
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 10f), 4000, null) // Configurar ZOOM de val coordenadas, 10f: quanto de Zoom, 6000: velocidade do Zoom 4.5s
        Toast.makeText(this, "Coordenadas do Marcador:", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment() // método criar mapa
    }

    // Fragment para carregar o mapa
    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment // função para criar mapa do tipo: as SupportMapFragment
        mapFragment.getMapAsync(this) // chamada para OnMapReadyCallback
    }

    // Função para verificar/comparar se a permissão de localização  esta ativada
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return // caso o mapa não tenha sido inicializado retorne...
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true // Verifica permissão ativada
        } else {
            requestLocationPermission() // Verifica permissão não ativada
        }
    }

    // Função solicitar permissão de localização
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) { // Pedido para ativar permissão
            Toast.makeText(this, "Vá em ajustes e ative permissões para usar o botão Localizar em tempo real", Toast.LENGTH_LONG).show() // Toast ativar permissão
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    // Verifica a resposta da solicitação
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        when (requestCode){ // Quando: REQUEST_CODE_LOCATION não esta vazia && const val for 0
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true // Ativa localização em tempo real
            } else {
                Toast.makeText(this, "Para ativar sua localização vá em ajustes e ative permissões", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Vá em ajustes e ative permissões de localização", Toast.LENGTH_SHORT).show()
        }

    }

   // Botão Localizar do canto superior direito da tela
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Localizar", Toast.LENGTH_SHORT).show()
        return false
   }
    // Mostrar Latitude e Longitude no mapa
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Latitude: ${p0.latitude}, Longitude: ${p0.longitude}", Toast.LENGTH_LONG).show()
    }
}