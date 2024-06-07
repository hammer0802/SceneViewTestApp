package com.example.sceneviewtestapp

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sceneviewtestapp.ui.theme.SceneViewTestAppTheme
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.addAugmentedImage
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val childNodes = rememberNodes()
            val augmentedImageNodes = mutableListOf<AugmentedImageNode>()
            SceneViewTestAppTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ARScene(
                        modifier = Modifier.fillMaxSize(),
                        engine = engine,
                        modelLoader = modelLoader,
                        childNodes = childNodes,
                        sessionConfiguration = { session, config ->
                            config.addAugmentedImage(
                                session,
                                "rabbit",
                                assets.open("augmentedimages/rabbit.jpg")
                                    .use { BitmapFactory.decodeStream(it) }
                            )
                        },
                        onSessionUpdated = { _, updatedFrame ->
                            updatedFrame.getUpdatedAugmentedImages().forEach { augmentedImage ->
                                if (augmentedImageNodes.none { it.imageName == augmentedImage.name }) {
                                    val augmentedImageNode = AugmentedImageNode(engine, augmentedImage).apply {
                                        when (augmentedImage.name) {
                                            "rabbit" -> addChildNode(
                                                ModelNode(
                                                    modelInstance = modelLoader.createModelInstance(
                                                        assetFileLocation = "models/rabbit.glb"
                                                    ),
                                                    scaleToUnits = 0.1f,
                                                    centerOrigin = Position(0.0f)
                                                )
                                            )
                                        }
                                    }
                                    childNodes.add(augmentedImageNode)
                                    augmentedImageNodes += augmentedImageNode
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}