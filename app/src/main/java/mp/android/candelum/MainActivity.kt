package mp.android.candelum

import android.icu.text.CaseMap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.android.candelum.lamp.LampViewModel
import mp.android.candelum.ui.lamp.LampScreen
import mp.android.candelum.ui.theme.CandelumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CandelumTheme {
                val vm: LampViewModel = viewModel()
                val state by vm.state.collectAsState()
                Scaffold { padding ->
                    LampScreen(
                        state = state,
                        onConnect = vm::connect,
                        onDisconnect = vm::disconnect,
                        onPower = vm::setPower,
                        onColor = vm::setColor,
                        onMode = vm::setMode,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}

@Composable
fun Screen(title: String, subtitle:String, modifier: Modifier = Modifier) {
    val image = painterResource(R.drawable.esp32)
    Box(modifier) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5f,
            modifier = modifier
        )
        Title(
            title = title,
            subtitle = subtitle,
            modifier = modifier
                .padding(20.dp)
        )
    }
}
@Composable
fun Title(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 50.sp,
            lineHeight = 50.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            fontSize = 17.sp,
            modifier = Modifier
                .padding(1.dp)
                .align(alignment = Alignment.End)
        )
    }
}

@Preview(showBackground = true,
        showSystemUi = true)
@Composable
fun TitlePreview() {
    CandelumTheme {
        Screen("Candelum", "App for smart lights", modifier = Modifier.fillMaxSize())
    }
}